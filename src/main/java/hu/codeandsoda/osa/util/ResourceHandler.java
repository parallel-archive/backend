package hu.codeandsoda.osa.util;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.MessageFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.amazonaws.services.s3.model.S3Object;

@Service
public class ResourceHandler {

    private static Logger logger = LoggerFactory.getLogger(ResourceHandler.class);

    public void closeInputStream(InputStream inputStream, String action, String actionParameters) {
        String error = "Could not close InputStream resource";
        close(inputStream, error, action, actionParameters);
    }

    public void closeOutputStream(OutputStream outputStream, String action, String actionParameters) {
        String error = "Could not close OutputStream resource";
        close(outputStream, error, action, actionParameters);
    }

    public void closeS3Object(S3Object s3Object, String action, String actionParameters) {
        String error = "Could not close S3Object resource";
        close(s3Object, error, action, actionParameters);
    }

    private void close(Closeable closeable, String error, String action, String actionParameters) {
        if (null != closeable) {
            try {
                closeable.close();
            } catch (IOException e) {
                String logMessage = MessageFormat.format("action={0}, status=warning, error={1}, {2}, exception={3}", error, action, actionParameters, e.getMessage());
                logger.warn(logMessage, e);
            }
        }
    }

}
