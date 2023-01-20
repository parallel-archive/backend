package hu.codeandsoda.osa.collection.validator;

import java.text.MessageFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import hu.codeandsoda.osa.account.user.domain.User;
import hu.codeandsoda.osa.collection.sevice.CollectionService;
import hu.codeandsoda.osa.util.ErrorCode;

@Component
public class DeleteCollectionRequestDataValidator implements Validator {

    private static Logger logger = LoggerFactory.getLogger(DeleteCollectionRequestDataValidator.class);

    @Autowired
    private CollectionService collectionService;

    @Override
    public boolean supports(Class<?> clazz) {
        return Long.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Long collectionId = (Long) target;

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = user.getId();

        if (!collectionService.collectionExists(collectionId, userId)) {
            String errorCode = ErrorCode.INVALID_COLLECTION_ID.toString();
            String logMessage = MessageFormat.format("action=deleteCollection, status=error, userId={0}, errorCode={1}", userId, errorCode);
            errors.reject(errorCode, "Invalid collection id.");
            logger.error(logMessage);
        }
    }

}
