package hu.codeandsoda.osa.exception;

import java.util.ArrayList;
import java.util.List;

import org.springframework.validation.ObjectError;

public class BusinessException extends Exception {

    private final List<ObjectError> errors;

    public BusinessException() {
        super();
        errors = new ArrayList<>();
    }

    public BusinessException(String message, List<ObjectError> errors) {
        super(message);
        this.errors = errors;
    }

    public List<ObjectError> getErrors() {
        return errors;
    }

}
