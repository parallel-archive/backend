package hu.codeandsoda.osa.ocr.validation;

import java.text.MessageFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import hu.codeandsoda.osa.account.user.domain.User;
import hu.codeandsoda.osa.document.domain.Document;
import hu.codeandsoda.osa.document.service.DocumentService;
import hu.codeandsoda.osa.util.ErrorCode;

@Component
public class OcrRequestValidator implements Validator {

    private static Logger logger = LoggerFactory.getLogger(OcrRequestValidator.class);

    @Autowired
    private DocumentService documentService;

    @Override
    public boolean supports(Class<?> clazz) {
        return Long.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Long documentId = (Long) target;

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = user.getId();

        validateIfDocumentExists(documentId, userId, errors);

        if (!errors.hasErrors()) {
            validateDocumentStatus(documentId, errors);
        }
    }

    private void validateIfDocumentExists(Long documentId, Long userId, Errors errors) {
        boolean documentExists = documentService.userDocumentExist(documentId, userId);
        if (!documentExists) {
            String errorCode = ErrorCode.DOCUMENT_NOT_FOUND.toString();
            String logMessage = MessageFormat.format("action=generateOcr, status=error, userId={0}, documentId={1}, errorCode={2}", userId, documentId, errorCode);
            errors.reject(errorCode, "Invalid document ID.");
            logger.error(logMessage);
        }
    }

    private void validateDocumentStatus(Long documentId, Errors errors) {
        Document document = documentService.loadById(documentId);
        boolean publishInProgress = documentService.isPublishInProgress(document);
        if (publishInProgress) {
            Long userId = document.getUser().getId();
            Long publishedDocumentId = document.getPublishedDocument().getId();

            String errorCode = ErrorCode.DOCUMENT_ALREADY_PUBLISHED.toString();
            errors.reject(errorCode, "Invalid document ID.");

            String logMessage = MessageFormat.format("action=generateOcr, status=error, userId={0}, documentId={1}, publishedDocumentId={2}, errorCode={3}", userId, documentId,
                    publishedDocumentId, errorCode);
            logger.error(logMessage);
        }
    }

}
