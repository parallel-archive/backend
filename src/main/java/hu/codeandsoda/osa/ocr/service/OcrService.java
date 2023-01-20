package hu.codeandsoda.osa.ocr.service;

import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;

import com.amazonaws.services.s3.model.S3Object;
import com.google.cloud.vision.v1.AnnotateImageRequest;
import com.google.cloud.vision.v1.AnnotateImageResponse;
import com.google.cloud.vision.v1.BatchAnnotateImagesResponse;
import com.google.cloud.vision.v1.Feature;
import com.google.cloud.vision.v1.Image;
import com.google.cloud.vision.v1.ImageAnnotatorClient;
import com.google.common.collect.Lists;
import com.google.protobuf.ByteString;

import hu.codeandsoda.osa.account.user.domain.User;
import hu.codeandsoda.osa.document.domain.DocumentImage;
import hu.codeandsoda.osa.document.service.DocumentImageService;
import hu.codeandsoda.osa.general.data.ResponseId;
import hu.codeandsoda.osa.general.data.ResponseMessage;
import hu.codeandsoda.osa.general.data.ResponseMessageScope;
import hu.codeandsoda.osa.media.exception.MediaLoadingException;
import hu.codeandsoda.osa.media.service.AwsS3Service;
import hu.codeandsoda.osa.media.service.MediaService;
import hu.codeandsoda.osa.ocr.domain.OcrRateLimit;
import hu.codeandsoda.osa.ocr.exception.GenerateOcrError;
import hu.codeandsoda.osa.prometheus.service.PrometheusReporterService;
import hu.codeandsoda.osa.util.ErrorCode;
import hu.codeandsoda.osa.util.ResourceHandler;

@Service
public class OcrService {
    
    private static final int FULL_OCR_TEXT_INDEX = 0;

    private static final String GENERATE_OCR_ACTION = "generateOcr";

    private static Logger logger = LoggerFactory.getLogger(OcrService.class);

    @Autowired
    private MediaService mediaService;

    @Autowired
    private AwsS3Service awsS3Service;

    @Autowired
    private DocumentImageService documentImageService;

    @Autowired
    private OcrRateLimitService ocrRateLimitService;

    @Autowired
    private PrometheusReporterService prometheusReporterService;

    @Autowired
    private ResourceHandler resourceHandler;

    @Value("${osa.ocr.limit}")
    private int ocrLimit;

    @Value("${osa.ocr.upload.batch.limit}")
    private int ocrUploadBatchLimit;

    public List<ResponseMessage> generateOcrFromDocumentImages(Long documentId, User user, Errors errors) throws MediaLoadingException, GenerateOcrError {
        List<DocumentImage> documentImages = documentImageService.loadByDocumentIdOrderedByIndex(documentId);
        List<ResponseMessage> responseMessages = generateImageOcrs(documentImages, documentId, user, errors);

        return responseMessages;
    }

    @Transactional
    public List<ResponseMessage> generateImageOcrs(List<DocumentImage> documentImages, Long documentId, User user, Errors errors) throws MediaLoadingException, GenerateOcrError {
        List<ResponseMessage> responseMessages = new ArrayList<>();
        List<DocumentImage> documentImagesWithoutOcr = new ArrayList<>();
        try (ImageAnnotatorClient client = ImageAnnotatorClient.create()) {

            ZonedDateTime start = ZonedDateTime.now();

            List<AnnotateImageRequest> requests = collectImageRequests(documentImages, documentImagesWithoutOcr, user, responseMessages, errors);
            reportOcrImageRequestDataToPrometheus(requests, documentId);

            List<AnnotateImageResponse> responses = loadOcrResponses(client, requests);

            updateDocumentImages(responses, documentImagesWithoutOcr, documentId, errors);

            reportOcrRequestDurationToPrometheus(start, documentId);

        } catch (IOException e) {
            String logMessage = MessageFormat.format("action=generateOcr, status=warning, error=Could not close ImageAnnotatorClient resource., error={0}, ", e.getMessage());
            logger.warn(logMessage, e);
        } catch (Exception e) {
            handleGenerateOcrsError(e, documentId, errors);
        }

        documentImageService.saveAll(documentImagesWithoutOcr);

        String logMessage = MessageFormat.format("action=generateOcr, status=success, userId={0}, documentId={1}, message=Saved generated OCRs to document images", user.getId(),
                documentId);
        logger.info(logMessage);

        return responseMessages;
    }

    public int loadUserOcrRateLimit(User user) {
        OcrRateLimit ocrRateLimit = ocrRateLimitService.loadByUser(user);
        int page = ocrRateLimit.getPage();
        return page;
    }

    private List<AnnotateImageRequest> collectImageRequests(List<DocumentImage> documentImages, List<DocumentImage> documentImagesWithoutOcr, User user,
            List<ResponseMessage> responseMessages, Errors errors)
            throws MediaLoadingException {
        OcrRateLimit ocrRateLimit = ocrRateLimitService.loadByUser(user);

        List<AnnotateImageRequest> requests = new ArrayList<>();
        for (DocumentImage documentImage : documentImages) {
            Long documentImageId = documentImage.getId(); 
            
            final boolean imageHasOcrText = StringUtils.hasText(documentImage.getOriginalOcr());
            if (!imageHasOcrText) {

                final boolean userWithinOcrPageLimit = ocrLimit > ocrRateLimit.getPage();
                if (userWithinOcrPageLimit) {
                    addImageRequest(documentImage, requests, errors);

                    documentImagesWithoutOcr.add(documentImage);
                    incrementOcrRateLimit(ocrRateLimit);
                } else {
                    handleOcrPageLimitReachedError(documentImageId, responseMessages);
                }
            } else {
                String logMessage = MessageFormat.format("action=generateOcr, status=error, imageId={0}, message=OCR already generated", documentImageId);
                logger.warn(logMessage);
            }
        }

        ocrRateLimitService.save(ocrRateLimit);

        return requests;
    }

    private ByteString loadImage(String imageKey, Errors errors) throws MediaLoadingException {
        S3Object s3Object = awsS3Service.loadImage(imageKey, errors);
        InputStream inputStream = s3Object.getObjectContent().getDelegateStream();
        ByteString imageBytes = ByteString.EMPTY;
        try {
            imageBytes = ByteString.readFrom(inputStream);
        } catch (IOException e) {
            String logMessage = MessageFormat.format("action=generateOcr, status=error, key={0}, error={1}, ", imageKey, e.getMessage());
            logger.error(logMessage, e);
            errors.reject(ErrorCode.MEDIA_LOADING_ERROR.toString(), "Could not load image as ByteString from AWS.");

            throw new MediaLoadingException("Could not load media.", errors.getAllErrors());
        } finally {
            String actionParameters = MessageFormat.format("imageKey={0}", imageKey);
            resourceHandler.closeS3Object(s3Object, GENERATE_OCR_ACTION, actionParameters);
            resourceHandler.closeInputStream(inputStream, GENERATE_OCR_ACTION, actionParameters);
        }

        return imageBytes;
    }

    private void addImageRequest(DocumentImage documentImage, List<AnnotateImageRequest> requests, Errors errors) throws MediaLoadingException {
        String imageKey = mediaService.extractS3ObjectKeyFromUrl(documentImage.getUrl());
        ByteString imageBytes = loadImage(imageKey, errors);

        Image image = Image.newBuilder().setContent(imageBytes).build();
        Feature feature = Feature.newBuilder().setType(Feature.Type.TEXT_DETECTION).build();
        AnnotateImageRequest request = AnnotateImageRequest.newBuilder().addFeatures(feature).setImage(image).build();
        requests.add(request);

        String logMessage = MessageFormat.format("action=generateOcr, status=info, imageId={0}, message=Image added to OCR requests", documentImage.getId());
        logger.info(logMessage);
    }

    private void incrementOcrRateLimit(OcrRateLimit ocrRateLimit) {
        int page = ocrRateLimit.getPage();
        page++;
        ocrRateLimit.setPage(page);
    }

    private List<AnnotateImageResponse> loadOcrResponses(ImageAnnotatorClient client, List<AnnotateImageRequest> requests) {
        List<AnnotateImageResponse> responses = new ArrayList<>();
        List<List<AnnotateImageRequest>> requestBatches = Lists.partition(requests, ocrUploadBatchLimit);

        for (List<AnnotateImageRequest> requestBatch : requestBatches) {
            sendBatchRequests(client, requestBatch, responses);
        }

        return responses;
    }

    private void sendBatchRequests(ImageAnnotatorClient client, List<AnnotateImageRequest> batchRequests, List<AnnotateImageResponse> responses) {
        String logMessage = MessageFormat.format("action=generateOcr, status=info, message=Send annotate images requests, batchSize={0}", batchRequests.size());
        logger.info(logMessage);

        BatchAnnotateImagesResponse response = client.batchAnnotateImages(batchRequests);
        List<AnnotateImageResponse> batchResponses = response.getResponsesList();
        responses.addAll(batchResponses);

        logger.info("action=generateOcr, status=info, message=Received annotate images responses");
    }

    private void updateDocumentImages(List<AnnotateImageResponse> responses, List<DocumentImage> documentImagesWithoutOcr, Long documentId, Errors errors) throws GenerateOcrError {
        int index = 0;
        for (AnnotateImageResponse res : responses) {
            DocumentImage documentImage = documentImagesWithoutOcr.get(index);
            Long imageId = documentImage.getId();

            if (res.hasError()) {
                handleGenerateOcrFromImageError(res.getError().getMessage(), imageId, documentId, errors);
            } else if (res.getTextAnnotationsCount() > 0) {
                String ocrText = res.getTextAnnotations(FULL_OCR_TEXT_INDEX).getDescription();
                documentImage.setOriginalOcr(ocrText);
                documentImage.setEditedOcr(ocrText);

                String logMessage = MessageFormat.format("action=generateOcr, status=success, imageId={0}", imageId);
                logger.info(logMessage);
            }
            index++;
        }
    }

    private void handleGenerateOcrFromImageError(String message, Long imageId, Long documentId, Errors errors) throws GenerateOcrError {
        String errorCode = ErrorCode.GENERATE_OCR_ERROR.toString();
        String logMessage = MessageFormat.format("action=generateOcr, status=error, imageId={0}, documentId={1}, errorCode={2}, errorMessage={3}", imageId, documentId, errorCode,
                message);
        logger.error(logMessage);

        String errorMessage = "Could not generate OCR from document images.";
        errors.reject(errorCode, errorMessage);
        throw new GenerateOcrError(errorMessage, errors.getAllErrors());
    }

    private void handleGenerateOcrsError(Exception exception, Long documentId, Errors errors) throws GenerateOcrError {
        String errorCode = ErrorCode.GENERATE_OCR_ERROR.toString();
        String exceptionMessage = exception.getMessage();

        String logMessage = MessageFormat.format("action=generateOcr, status=error, documentId={0}, errorCode={1}, errorMessage={2}", documentId, errorCode, exceptionMessage);
        logger.error(logMessage, exception);

        prometheusReporterService.sendGenerateOcrError(exceptionMessage);

        String errorMessage = "Could not generate OCR from document images.";
        errors.reject(errorCode, errorMessage);
        throw new GenerateOcrError(errorMessage, errors.getAllErrors());
    }

    private void handleOcrPageLimitReachedError(Long documentImageId, List<ResponseMessage> responseMessages) {
        String message = MessageFormat.format("Could not generate OCR for image of ID : {0}. OCR limit reached.", documentImageId);
        ResponseMessage responseMessage = new ResponseMessage.ResponseMessageBuilder().setId(ResponseId.OCR_LIMIT_REACHED).setMessage(message)
                .setResponseMessageScope(ResponseMessageScope.WARNING).build();
        responseMessages.add(responseMessage);

        String logMessage = MessageFormat.format("action=generateOcr, status=error, imageId={0}, message=OCR limit reached, limit={1}", documentImageId, ocrLimit);
        logger.error(logMessage);
    }

    private void reportOcrRequestDurationToPrometheus(ZonedDateTime start, Long documentId) {
        ZonedDateTime end = ZonedDateTime.now();
        long requestDurationSeconds = Duration.between(start, end).getSeconds();
        prometheusReporterService.updateOcrRequestDurationSecondsCounter(documentId, requestDurationSeconds);
    }

    private void reportOcrImageRequestDataToPrometheus(List<AnnotateImageRequest> requests, Long documentId) {
        int imageRequestNumber = requests.size();
        prometheusReporterService.updateOcrRequestImageNumberCounter(documentId, imageRequestNumber);

        int imageRequestSize = calculateTotalImageRequestSize(requests);
        prometheusReporterService.updateOcrRequestImagesTotalSizeCounter(documentId, imageRequestSize);
    }

    private int calculateTotalImageRequestSize(List<AnnotateImageRequest> requests) {
        int imageRequestSize = 0;
        for (AnnotateImageRequest request : requests) {
            int imageSize = request.getImage().getContent().size();
            imageRequestSize += imageSize;
        }
        return imageRequestSize;
    }

}
