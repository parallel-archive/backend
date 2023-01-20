package hu.codeandsoda.osa.ocr.exception;

import java.util.List;

import org.springframework.validation.ObjectError;

import hu.codeandsoda.osa.exception.BusinessException;

public class GenerateOcrError extends BusinessException {

    public GenerateOcrError(String message, List<ObjectError> errors) {
        super(message, errors);
    }
}
