package hu.codeandsoda.osa.registration.exception;

import java.util.List;

import org.springframework.validation.ObjectError;

import hu.codeandsoda.osa.exception.BusinessException;

public class RegistrationException extends BusinessException {

    public RegistrationException(String message, List<ObjectError> errors) {
        super(message, errors);
    }

}
