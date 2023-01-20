package hu.codeandsoda.osa.collection.validator;

import java.text.MessageFormat;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import hu.codeandsoda.osa.account.user.domain.User;
import hu.codeandsoda.osa.collection.data.CollectionContentRequestData;
import hu.codeandsoda.osa.collection.domain.Collection;
import hu.codeandsoda.osa.collection.sevice.CollectionService;
import hu.codeandsoda.osa.documentpublish.domain.PublishedDocument;
import hu.codeandsoda.osa.documentpublish.service.PublishedDocumentService;
import hu.codeandsoda.osa.util.ErrorCode;

@Component
public class AddCollectionContentRequestDataValidator {

    private static Logger logger = LoggerFactory.getLogger(AddCollectionContentRequestDataValidator.class);

    @Autowired
    private CollectionService collectionService;

    @Autowired
    private PublishedDocumentService publishedDocumentService;

    public void validate(Object target, Errors errors) {
        CollectionContentRequestData requestData = (CollectionContentRequestData) target;
        Long collectionId = requestData.getCollectionId();
        String publishedDocumentHash = requestData.getPublishedDocumentHash();

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = user.getId();
        
        boolean publishedDocumentExists = validateIfPublishedDocumentExists(publishedDocumentHash, collectionId, userId, errors);

        boolean publishedDocumentStatusIsPublished = false;
        if (publishedDocumentExists) {
            publishedDocumentStatusIsPublished = validatePublishedDocumentStatus(publishedDocumentHash, collectionId, userId, errors);
        }

        Collection collection = collectionService.loadCollectionByIdAndUserId(collectionId, userId);
        boolean collectionExists = validateIfCollectionExists(collection, publishedDocumentHash, collectionId, userId, errors);
        
        if (collectionExists && publishedDocumentExists && publishedDocumentStatusIsPublished) {
            validateIfCollectionContainsPublishedDocument(collection, publishedDocumentHash, collectionId, userId, errors);
        }
    }

    private boolean validateIfPublishedDocumentExists(String publishedDocumentHash, Long collectionId, Long userId, Errors errors) {
        boolean publishedDocumentExists = publishedDocumentService.existsByHash(publishedDocumentHash);
        if (!publishedDocumentExists) {
            String errorCode = ErrorCode.INVALID_DOCUMENT_HASH.toString();
            String logMessage = MessageFormat.format(
                    "action=addPublishedDocumentToCollection, status=error, userId={0}, publishedDocumentHash={1}, collectionId={2}, errorCode={3}", userId, publishedDocumentHash,
                    collectionId, errorCode);
            errors.reject(errorCode, "Invalid published document hash.");
            logger.error(logMessage);
        }
        return publishedDocumentExists;
    }

    private boolean validatePublishedDocumentStatus(String publishedDocumentHash, Long collectionId, Long userId, Errors errors) {
        boolean publishedDocumentStatusIsPublished = publishedDocumentService.isDocumentPublished(publishedDocumentHash);
        if (!publishedDocumentStatusIsPublished) {
            String errorCode = ErrorCode.INVALID_DOCUMENT_HASH.toString();
            errors.reject(errorCode, "Invalid published document hash.");

            String logMessage = MessageFormat.format(
                    "action=addPublishedDocumentToCollection, status=error, userId={0}, publishedDocumentHash={1}, collectionId={2}, errorCode={3}", userId, publishedDocumentHash,
                    collectionId, ErrorCode.PUBLISH_IN_PROGRESS);
            logger.error(logMessage);
        }
        return publishedDocumentStatusIsPublished;
    }

    private boolean validateIfCollectionExists(Collection collection, String publishedDocumentHash, Long collectionId, Long userId, Errors errors) {
        boolean collectionExists = null != collection;
        if (!collectionExists) {
            String errorCode = ErrorCode.INVALID_COLLECTION_ID.toString();
            String logMessage = MessageFormat.format(
                    "action=addPublishedDocumentToCollection, status=error, userId={0}, publishedDocumentHash={1}, collectionId={2}, errorCode={3}", userId, publishedDocumentHash,
                    collectionId, errorCode);
            errors.reject(errorCode, "Invalid collection id.");
            logger.error(logMessage);
        }
        return collectionExists;
    }

    private void validateIfCollectionContainsPublishedDocument(Collection collection, String publishedDocumentHash, Long collectionId, Long userId, Errors errors) {
        List<PublishedDocument> documents = collection.getDocuments();
        boolean documentAlreadyAdded = documents.stream().anyMatch(d -> d.getHash().equals(publishedDocumentHash));
        if (documentAlreadyAdded) {
            String errorCode = ErrorCode.COLLECTION_CONTAINS_DOCUMENT.toString();
            String logMessage = MessageFormat.format(
                    "action=addPublishedDocumentToCollection, status=error, userId={0}, publishedDocumentHash={1}, collectionId={2}, errorCode={3}", userId, publishedDocumentHash,
                    collectionId, errorCode);
            errors.reject(errorCode, "Invalid collection id or published document hash.");
            logger.error(logMessage);
        }
    }

}
