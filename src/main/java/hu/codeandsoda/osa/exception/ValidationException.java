package hu.codeandsoda.osa.exception;

import java.util.List;

import org.springframework.validation.ObjectError;

public class ValidationException extends BusinessException {

    public ValidationException(String message, List<ObjectError> errors) {
        super(message, errors);
    }

}
