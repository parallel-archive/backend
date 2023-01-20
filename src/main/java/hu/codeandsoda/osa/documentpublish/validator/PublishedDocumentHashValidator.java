package hu.codeandsoda.osa.documentpublish.validator;

import java.text.MessageFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import hu.codeandsoda.osa.documentpublish.service.PublishedDocumentService;
import hu.codeandsoda.osa.util.ErrorCode;

@Component
public class PublishedDocumentHashValidator implements Validator {

    private static Logger logger = LoggerFactory.getLogger(PublishedDocumentHashValidator.class);

    @Autowired
    private PublishedDocumentService publishedDocumentService;

    @Override
    public boolean supports(Class<?> clazz) {
        return String.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        String hash = (String) target;

        if (!publishedDocumentService.existsByHash(hash) || !publishedDocumentService.isDocumentPublished(hash)) {
            String errorCode = ErrorCode.INVALID_DOCUMENT_HASH.toString();
            errors.reject(errorCode, "Invalid document hash. Document does not exist.");
            String logMessage = MessageFormat.format("action=loadPublishedDocument, status=error, errorCode={0}, hash={1}", errorCode, hash);
            logger.error(logMessage);
        }
    }

}
