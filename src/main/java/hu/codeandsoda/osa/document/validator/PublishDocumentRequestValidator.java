package hu.codeandsoda.osa.document.validator;

import java.text.MessageFormat;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import hu.codeandsoda.osa.account.user.domain.User;
import hu.codeandsoda.osa.document.domain.Document;
import hu.codeandsoda.osa.document.domain.DocumentImage;
import hu.codeandsoda.osa.document.domain.DocumentMetaData;
import hu.codeandsoda.osa.document.service.DocumentService;
import hu.codeandsoda.osa.util.ErrorCode;

@Component
public class PublishDocumentRequestValidator implements Validator {

    private static Logger logger = LoggerFactory.getLogger(PublishDocumentRequestValidator.class);

    @Autowired
    private DocumentService documentService;

    @Override
    public boolean supports(Class<?> clazz) {
        return Document.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Document document = (Document) target;

        validateDocumentId(document, errors);
        
        if (!errors.hasErrors()) {
            validateDocumentStatus(document, errors);
        }
        
        if (!errors.hasErrors()) {
            Long documentId = document.getId();
            validateDocumentMetaData(document.getMetaData(), documentId, errors);
            validateDocumentImages(document.getImages(), documentId, errors);
        }
    }

    private void validateDocumentId(Document document, Errors errors) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = user.getId();
        if (null == document || !isUserIdValid(document, userId)) {
            Long documentId = null == document ? null : document.getId();

            String errorCode = ErrorCode.DOCUMENT_NOT_FOUND.toString();
            errors.reject(errorCode, "Invalid document ID.");

            String logMessage = MessageFormat.format("action=publishDocument, status=error, userId={0}, documentId={1}, errorCode={2}", userId, documentId, errorCode);
            logger.error(logMessage);
        }
    }

    private boolean isUserIdValid(Document document, Long userId) {
        boolean userIdValid = userId.equals(document.getUser().getId());
        return userIdValid;
    }

    private void validateDocumentStatus(Document document, Errors errors) {
        boolean publishInProgress = documentService.isPublishInProgress(document);
        if (publishInProgress) {
            Long userId = document.getUser().getId();
            Long documentId = document.getId();
            Long publishedDocumentId = document.getPublishedDocument().getId();

            String errorCode = ErrorCode.DOCUMENT_ALREADY_PUBLISHED.toString();
            errors.reject(errorCode, "Invalid document ID.");

            String logMessage = MessageFormat.format("action=publishDocument, status=error, userId={0}, documentId={1}, publishedDocumentId={2}, errorCode={3}", userId, documentId,
                    publishedDocumentId, errorCode);
            logger.error(logMessage);
        }
    }

    // Required Published Document Metadata fields :
    // - Original Title
    // - Archive Name
    // - Reference Code
    private void validateDocumentMetaData(DocumentMetaData metaData, Long documentId, Errors errors) {
        if (!StringUtils.hasText(metaData.getOriginalTitle())) {
            handleMissingMetaDataFieldError("Original Title", documentId, errors);
        }
        if (!StringUtils.hasText(metaData.getArchiveName())) {
            handleMissingMetaDataFieldError("Archive Name", documentId, errors);
        }
        if (!StringUtils.hasText(metaData.getReferenceCode())) {
            handleMissingMetaDataFieldError("Reference code", documentId, errors);
        }

    }

    private void validateDocumentImages(List<DocumentImage> images, Long documentId, Errors errors) {
        if (images.isEmpty()) {
            String errorCode = ErrorCode.DOCUMENT_HAS_NO_IMAGES.toString();
            String logMessage = MessageFormat.format("action=publishDocument, status=error, documentId={0}, errorCode={1}", documentId, errorCode);
            errors.reject(errorCode, "Document has no image to publish.");
            logger.error(logMessage);
        }
    }

    private void handleMissingMetaDataFieldError(String fieldName, Long documentId, Errors errors) {
        String errorCode = ErrorCode.MISSING_DOCUMENT_METADATA_FIELD.toString();
        String logMessage = MessageFormat.format("action=publishDocument, status=error, documentId={0}, errorCode={1}, fieldName={2}", documentId, errorCode, fieldName);
        String errorMessage = MessageFormat.format("Document field is missing: {0}", fieldName);
        errors.reject(errorCode, errorMessage);
        logger.error(logMessage);
    }
}
