package hu.codeandsoda.osa.media.service;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.Base64;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.web.multipart.MultipartFile;

import hu.codeandsoda.osa.util.ErrorCode;
import hu.codeandsoda.osa.util.ResourceHandler;

@Service
public class MultipartFileService {

    private static final String UPLOAD_MEDIA_TO_AWS_S3_ACTION = "uploadMediaToAwsS3";

    private static Logger logger = LoggerFactory.getLogger(MultipartFileService.class);

    @Autowired
    private ResourceHandler resourceHandler;

    public String getFileContentType(MultipartFile file, Long userId, Errors errors) {
        InputStream is = null;
        BufferedInputStream bufferedInputStream = null;
        String type = null;
        try {
            is = getFileInputStream(file, userId, errors);
            bufferedInputStream = new BufferedInputStream(is);

            // returns null if not wrapped into BufferedInputStream
            // Manages limited set of content types!
            // Image types: image/gif, image/x-bitmap, image/x-pixmap, image/png, image/jpeg, image/tiff, image/vnd.fpx
            type = URLConnection.guessContentTypeFromStream(bufferedInputStream);

            String log = MessageFormat.format("MultipartFile content type loaded. File name : {0}, type: {1}", file.getOriginalFilename(), type);
            logger.info(log);
        } catch (IOException e) {
            String errorCode = ErrorCode.MEDIA_UPLOAD_ERROR.toString();
            String logMessage = MessageFormat.format("action={0}, status=error, userId={1}, media={2}, error={3}", UPLOAD_MEDIA_TO_AWS_S3_ACTION, userId,
                    file.getOriginalFilename(), e.getMessage());
            errors.reject(errorCode, "Could not load file content type.");
            logger.error(logMessage);
        } finally {
            String actionParameters = MessageFormat.format("fileName={0}, userId={1}", file.getOriginalFilename(), userId);
            resourceHandler.closeInputStream(is, UPLOAD_MEDIA_TO_AWS_S3_ACTION, actionParameters);
            resourceHandler.closeInputStream(bufferedInputStream, actionParameters, actionParameters);
        }

        return type;
    }

    public InputStream getFileInputStream(MultipartFile file, Long userId, Errors errors) {
        InputStream is = null;
        InputStream returnIs = null;
        try {
            is = file.getInputStream();

            String text = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8)).lines().collect(Collectors.joining("\n"));
            // If image is converted from HEIC to PNG we should cut "data:image/png;base64," and decode the content
            if (text.startsWith("data")) {
                int index = text.indexOf(",");
                text = text.substring(index + 1);

                byte[] imageData = Base64.getDecoder().decode(text);
                returnIs = new ByteArrayInputStream(imageData);
            } else {
                returnIs = file.getInputStream();
            }

        } catch (IOException e) {
            String errorCode = ErrorCode.MEDIA_UPLOAD_ERROR.toString();
            String logMessage = MessageFormat.format("action=uploadMediaToAwsS3, status=error, userId={0}, media={1}, error={2}", userId, file.getOriginalFilename(),
                    e.getMessage());
            errors.reject(errorCode, "Could not load file content.");
            logger.error(logMessage);
        } finally {
            String actionParameters = MessageFormat.format("fileName={0}, userId={1}", file.getOriginalFilename(), userId);
            resourceHandler.closeInputStream(is, UPLOAD_MEDIA_TO_AWS_S3_ACTION, actionParameters);
        }

        return returnIs;
    }

}
