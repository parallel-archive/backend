package hu.codeandsoda.osa.pdf.exception;

import java.util.List;

import org.springframework.validation.ObjectError;

import hu.codeandsoda.osa.exception.BusinessException;

public class CreatePDFException extends BusinessException {

    public CreatePDFException(String message, List<ObjectError> errors) {
        super(message, errors);
    }

}

