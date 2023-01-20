package hu.codeandsoda.osa.account.user.exception;

import java.util.List;

import org.springframework.validation.ObjectError;

import hu.codeandsoda.osa.exception.BusinessException;

public class UserDeletionException extends BusinessException {

    public UserDeletionException(String message, List<ObjectError> errors) {
        super(message, errors);
    }

}

