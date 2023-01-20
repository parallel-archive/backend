package hu.codeandsoda.osa.ipfs.exception;

import java.util.List;

import org.springframework.validation.ObjectError;

import hu.codeandsoda.osa.exception.BusinessException;

public class IpfsUploadException extends BusinessException {

    public IpfsUploadException(String message, List<ObjectError> errors) {
        super(message, errors);
    }

}
