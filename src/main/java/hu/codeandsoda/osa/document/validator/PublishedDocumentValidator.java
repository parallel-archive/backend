package hu.codeandsoda.osa.document.validator;

import java.text.MessageFormat;
import java.time.Duration;
import java.time.ZonedDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;

import hu.codeandsoda.osa.documentpublish.data.PublishedDocumentStatus;
import hu.codeandsoda.osa.documentpublish.domain.PublishedDocument;
import hu.codeandsoda.osa.documentpublish.domain.PublishedDocumentMetaData;
import hu.codeandsoda.osa.util.ErrorCode;

@Component
public class PublishedDocumentValidator {

    private static Logger logger = LoggerFactory.getLogger(PublishedDocumentValidator.class);

    public void validate(PublishedDocument publishedDocument, Errors errors) {
        Long id = publishedDocument.getId();
        Long userId = publishedDocument.getUser().getId();

        ZonedDateTime start = ZonedDateTime.now();
        String startlog = MessageFormat.format("START Validate Published Document. Published Document ID : {0}. User ID : {1}. Current time is : {2}", id, userId, start);
        logger.info(startlog);

        validatePublishedDocumentMetaData(publishedDocument.getPublishedDocumentMetaData(), id, errors);
        validatePublishedDocumentFields(publishedDocument, errors);

        long duration = Duration.between(start, ZonedDateTime.now()).getSeconds();
        String endLog = MessageFormat.format("END Validate Published Document. Published Document ID : {0}. User ID : {1}. Duration : {2} seconds", id, userId, duration);
        logger.info(endLog);
    }

    // Required Published Document Metadata fields :
    // - Original Title
    // - Archive Name
    // - Reference Code
    private void validatePublishedDocumentMetaData(PublishedDocumentMetaData metaData, Long documentId, Errors errors) {
        if (!StringUtils.hasText(metaData.getOriginalTitle())) {
            handleMissingFieldError("Original Title", documentId, errors);
        }
        if (!StringUtils.hasText(metaData.getArchiveName())) {
            handleMissingFieldError("Archive Name", documentId, errors);
        }
        if (!StringUtils.hasText(metaData.getReferenceCode())) {
            handleMissingFieldError("Reference Code", documentId, errors);
        }
    }

    private void validatePublishedDocumentFields(PublishedDocument publishedDocument, Errors errors) {
        Long documentId = publishedDocument.getId();
        if (!StringUtils.hasText(publishedDocument.getHash())) {
            handleMissingFieldError("Hash", documentId, errors);
        }
        if (!StringUtils.hasText(publishedDocument.getPdfUrl())) {
            handleMissingFieldError("Pdf url", documentId, errors);
        }
        if (!StringUtils.hasText(publishedDocument.getThumbnailUrl())) {
            handleMissingFieldError("Thumbnail url", documentId, errors);
        }
        if (null == publishedDocument.getCreatedAt()) {
            handleMissingFieldError("Created at date", documentId, errors);
        }
        if (publishedDocument.getViews() != 0) {
            handleInvalidFieldError("Views", documentId, errors);
        }
        if (!StringUtils.hasText(publishedDocument.getIpfsContentId())) {
            handleInvalidFieldError("IPFS content ID", documentId, errors);
        }
        if (PublishedDocumentStatus.IN_PROGRESS != publishedDocument.getStatus()) {
            handleInvalidFieldError("Published Document Status", documentId, errors);
        }
    }

    private void handleMissingFieldError(String fieldName, Long documentId, Errors errors) {
        String errorCode = ErrorCode.MISSING_PUBLISHED_DOCUMENT_FIELD.toString();
        handleFieldError(fieldName, "missing", errorCode, documentId, errors);
    }

    private void handleInvalidFieldError(String fieldName, Long documentId, Errors errors) {
        String errorCode = ErrorCode.INVALID_PUBLISHED_DOCUMENT_FIELD.toString();
        handleFieldError(fieldName, "invalid", errorCode, documentId, errors);
    }

    private void handleFieldError(String fieldName, String fieldStatus, String errorCode, Long documentId, Errors errors) {
        String errorMessage = MessageFormat.format("Published Document field is {0}: {1}", fieldStatus, fieldName);
        errors.reject(errorCode, errorMessage);

        String logMessage = MessageFormat.format("action=publishDocument, status=error, documentId={0}, errorCode={1}, fieldName={2}, fieldStatus={3}", documentId, errorCode,
                fieldName, fieldStatus);
        logger.error(logMessage);
    }

}
