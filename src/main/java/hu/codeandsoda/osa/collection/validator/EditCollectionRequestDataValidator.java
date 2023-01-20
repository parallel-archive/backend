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
import hu.codeandsoda.osa.collection.data.EditCollectionRequestData;
import hu.codeandsoda.osa.collection.sevice.CollectionService;
import hu.codeandsoda.osa.util.ErrorCode;

@Component
public class EditCollectionRequestDataValidator implements Validator {

    private static Logger logger = LoggerFactory.getLogger(EditCollectionRequestDataValidator.class);

    @Autowired
    private CollectionService collectionService;

    @Override
    public boolean supports(Class<?> clazz) {
        return EditCollectionRequestData.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        EditCollectionRequestData editCollectionRequestData = (EditCollectionRequestData) target;
        Long collectionId = editCollectionRequestData.getId();
        String collectionName = editCollectionRequestData.getName();

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = user.getId();

        validateCollectionId(collectionId, userId, errors);

        validateCollectionName(collectionName, collectionId, userId, errors);
    }

    private void validateCollectionId(Long collectionId, Long userId, Errors errors) {
        if (!collectionService.collectionExists(collectionId, userId)) {
            String errorCode = ErrorCode.INVALID_COLLECTION_ID.toString();
            String logMessage = MessageFormat.format("action=createCollection, status=error, userId={0}, errorCode={1}", userId, errorCode);
            errors.reject(errorCode, "Invalid collection id.");
            logger.error(logMessage);
        }
    }

    private void validateCollectionName(String collectionName, Long collectionId, Long userId, Errors errors) {
        if (!StringUtils.hasText(collectionName)) {
            handleInvalidCollectionNameError("Collection name is missing.", collectionId, userId, errors);
        } else if (collectionService.collectionNameExists(collectionName, userId)) {
            handleInvalidCollectionNameError("Collection already exists.", collectionId, userId, errors);
        }
    }

    private void handleInvalidCollectionNameError(String message, Long collectionId, Long userId, Errors errors) {
        String errorCode = ErrorCode.INVALID_COLLECTION_NAME.toString();
        String logMessage = MessageFormat.format("action=createCollection, status=error, collectionId={0}, userId={1}, errorCode={2}, errorMessage={3}", collectionId, userId,
                errorCode, message);
        errors.reject(errorCode, message);
        logger.error(logMessage);
    }

}
