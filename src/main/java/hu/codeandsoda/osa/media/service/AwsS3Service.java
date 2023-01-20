package hu.codeandsoda.osa.media.service;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.CopyObjectRequest;
import com.amazonaws.services.s3.model.DeleteObjectsRequest;
import com.amazonaws.services.s3.model.DeleteObjectsRequest.KeyVersion;
import com.amazonaws.services.s3.model.DeleteObjectsResult;
import com.amazonaws.services.s3.model.DeleteObjectsResult.DeletedObject;
import com.amazonaws.services.s3.model.MultiObjectDeleteException.DeleteError;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;

import hu.codeandsoda.osa.media.exception.MediaCopyException;
import hu.codeandsoda.osa.media.exception.MediaLoadingException;
import hu.codeandsoda.osa.media.exception.MediaUploadException;
import hu.codeandsoda.osa.prometheus.service.PrometheusReporterService;
import hu.codeandsoda.osa.util.ErrorCode;

@Service
public class AwsS3Service {

    private static Logger logger = LoggerFactory.getLogger(AwsS3Service.class);

    @Autowired
    private AmazonS3 s3client;

    @Autowired
    private PrometheusReporterService prometheusReporterService;

    @Value("${jsa.s3.bucket:bucket}")
    private String bucketName;

    public String upload(InputStream inputStream, String key, String contentType, Long userId, Errors errors) throws MediaUploadException {
        try {
            String urlEncodedUTF8Filename = URLEncoder.encode(key, "UTF-8");

            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentDisposition("filename=\"" + key + "\"; filename*=UTF-8''" + urlEncodedUTF8Filename);
            metadata.setContentType(contentType);
            metadata.setContentLength(inputStream.available());

            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, key, inputStream, metadata).withCannedAcl(CannedAccessControlList.PublicRead);
            s3client.putObject(putObjectRequest);

        } catch (UnsupportedEncodingException e) {
            handleMediaUploadError(userId, key, ErrorCode.AWS_S3_KEY_ENCODING_ERROR.toString(), "Could not encode AWS S3 key.", e, errors);
        } catch (Exception e) {
            handleMediaUploadError(userId, key, ErrorCode.MEDIA_UPLOAD_ERROR.toString(), "Could not upload media to AWS S3.", e, errors);
        }

        String url = s3client.getUrl(bucketName, key).toExternalForm();
        return url;
    }

    public S3Object loadImage(String key, Errors errors) throws MediaLoadingException {
        S3Object s3Object = null;
        try {
            s3Object = s3client.getObject(bucketName, key);
        } catch (Exception e) {
            handleMediaLoadingError(key, e, errors);
            throw new MediaLoadingException("Could not load media.", errors.getAllErrors());
        }
        return s3Object;
    }

    public List<String> deleteImages(List<String> keys) {
        List<String> deletedKeys = new ArrayList<>();

        if (keys.size() > 0) {
            List<KeyVersion> keyVersions = new ArrayList<>();
            for (String key : keys) {
                keyVersions.add(new KeyVersion(key));
                logger.info("scheduledTask=deleteImages, action=selectKeyToDeleteFromS3Bucket, status=success, key=" + key);
            }
            logger.info("scheduledTask=deleteImages, action=collectKeysToDeleteFromS3Bucket, status=success, size=" + keyVersions.size());

            DeleteObjectsRequest deleteObjectsRequest = new DeleteObjectsRequest(bucketName).withKeys(keyVersions).withQuiet(false);
            DeleteObjectsResult deleteObjectsResult = s3client.deleteObjects(deleteObjectsRequest);
            deletedKeys = collectDeletedKeysFromDeleteObjects(deleteObjectsResult.getDeletedObjects());
            logger.info("scheduledTask=deleteImages, action=deleteKeysFromS3Bucket, status=success, size=" + deletedKeys.size());
        } else {
            logger.info("scheduledTask=deleteImages, action=collectKeysToDeleteFromS3Bucket, status=warning, size=0, message=No images to delete.");
        }

        return deletedKeys;
    }

    public List<String> collectDeletedKeysFromDeleteObjects(List<DeletedObject> deletedObjects) {
        List<String> deletedKeys = deletedObjects.stream().map(k -> k.getKey()).collect(Collectors.toList());
        return deletedKeys;
    }

    public String copyImage(String sourceKey, String destinationKey, Errors errors) throws MediaCopyException {
        String url = null;
        try {
            CopyObjectRequest copyObjectRequest = new CopyObjectRequest(bucketName, sourceKey, bucketName, destinationKey);
            copyObjectRequest.setCannedAccessControlList(CannedAccessControlList.PublicRead);

            s3client.copyObject(copyObjectRequest);
            url = s3client.getUrl(bucketName, destinationKey).toString();
        } catch (Exception e) {
            String logMessage = MessageFormat.format("action=copyImageOnAws, status=error, key={0}, error={1}, sourceKey={2}, destinationKey={3}", url, e.getMessage(), sourceKey,
                    destinationKey);
            logger.error(logMessage, e);

            prometheusReporterService.sendCopyMediaToS3Error(e.getMessage());

            errors.reject(ErrorCode.MEDIA_LOADING_ERROR.toString(), "Could not copy image on AWS.");
            throw new MediaCopyException(logMessage, errors.getAllErrors());
        }
        return url;
    }

    public Map<String, String> collectDeleteImageErrors(List<DeleteError> errors) {
        Map<String, String> deleteImageErrors = new HashMap<>();
        for (DeleteError deleteError : errors) {
            deleteImageErrors.put(deleteError.getKey(), deleteError.getMessage());
        }
        return deleteImageErrors;
    }

    private void handleMediaUploadError(Long userId, String key, String errorCode, String error, Exception exception, Errors errors) throws MediaUploadException {
        String logMessage = MessageFormat.format("action=uploadMediaToAwsS3, status=error, userId={0}, key={1}, error={2}", userId, key, exception.getMessage());
        logger.error(logMessage, exception);

        prometheusReporterService.sendUploadMediaToS3Error(exception.getMessage());

        errors.reject(errorCode, error);
        throw new MediaUploadException(errorCode, errors.getAllErrors());

    }

    private void handleMediaLoadingError(String url, Exception exception, Errors errors) {
        String logMessage = MessageFormat.format("action=loadImageFromAwsS3, status=error, key={0}, error={1}, ", url, exception.getMessage());
        logger.error(logMessage, exception);
        errors.reject(ErrorCode.MEDIA_LOADING_ERROR.toString(), "Could not load image from AWS.");

        prometheusReporterService.sendLoadMediaFromS3Error(exception.getMessage());
    }

}
