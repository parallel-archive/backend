package hu.codeandsoda.osa.media.exception;

import java.util.List;

import org.springframework.validation.ObjectError;

import hu.codeandsoda.osa.exception.BusinessException;

public class MediaCopyException extends BusinessException {
    
    public MediaCopyException(String message, List<ObjectError> errors) {
        super(message, errors);
    }

}
