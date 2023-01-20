package hu.codeandsoda.osa.collection.validator;

import java.text.MessageFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import hu.codeandsoda.osa.account.user.domain.User;
import hu.codeandsoda.osa.collection.data.CollectionRequestData;
import hu.codeandsoda.osa.collection.sevice.CollectionService;
import hu.codeandsoda.osa.documentpublish.service.PublishedDocumentService;
import hu.codeandsoda.osa.util.ErrorCode;
import hu.codeandsoda.osa.util.OsaConstantUtil;

@Component
public class CollectionRequestDataValidator implements Validator {

    private static final String COLLECTION_NAME_FIELD = "name";

    private static Logger logger = LoggerFactory.getLogger(CollectionRequestDataValidator.class);

    @Autowired
    private CollectionService collectionService;

    @Autowired
    private PublishedDocumentService publishedDocumentService;

    @Override
    public boolean supports(Class<?> clazz) {
        return CollectionRequestData.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        CollectionRequestData collectionRequestData = (CollectionRequestData) target;
        String collectionName = collectionRequestData.getName();
        String publication = collectionRequestData.getPublication();

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = user.getId();

        validateCollectionName(collectionName, userId, errors);
        validatePublishedDocument(publication, userId, errors);
    }

    private void validateCollectionName(String collectionName, Long userId, Errors errors) {
        if (!StringUtils.hasText(collectionName)) {
            handleInvalidCollectionRequestError(COLLECTION_NAME_FIELD, "Collection name is missing.", userId, ErrorCode.INVALID_COLLECTION_NAME, errors);
        } else if (collectionService.collectionNameExists(collectionName, userId)) {
            handleInvalidCollectionRequestError(COLLECTION_NAME_FIELD, "Collection already exists.", userId, ErrorCode.INVALID_COLLECTION_NAME, errors);
        }
    }

    private void validatePublishedDocument(String publication, Long userId, Errors errors) {
        if (StringUtils.hasText(publication) && !publishedDocumentService.existsByHashAndPublishedStatus(publication)) {
            handleInvalidCollectionRequestError("publication", "Invalid Published Document hash.", userId, ErrorCode.PUBLISHED_DOCUMENT_NOT_FOUND, errors);
        }
     }

     private void handleInvalidCollectionRequestError(String field, String message, Long userId, ErrorCode errorCode, Errors errors) {
         String logMessage = MessageFormat.format("action=createCollection, status=error, userId={0}, errorCode={1}, errorMessage={2}", userId, errorCode,
                message);
        errors.rejectValue(field, OsaConstantUtil.TEMPLATE_FORM_EMPTY_ERROR_CODE, message);
        logger.error(logMessage);
    }

}
