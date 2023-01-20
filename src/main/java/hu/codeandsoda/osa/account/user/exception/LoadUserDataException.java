package hu.codeandsoda.osa.account.user.exception;

import java.util.List;

import org.springframework.validation.ObjectError;

import hu.codeandsoda.osa.exception.BusinessException;

public class LoadUserDataException extends BusinessException {

    public LoadUserDataException(String message, List<ObjectError> errors) {
        super(message, errors);
    }

}
