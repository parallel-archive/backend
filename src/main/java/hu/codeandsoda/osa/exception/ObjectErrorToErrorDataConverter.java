package hu.codeandsoda.osa.exception;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

import hu.codeandsoda.osa.general.data.ErrorData;

@Service
public class ObjectErrorToErrorDataConverter {

    public static List<ErrorData> convertObjectErrorsToErrorDatas(List<ObjectError> errors) {
        List<ErrorData> errorDatas = new ArrayList<>();
        for (ObjectError objectError : errors) {
            errorDatas.add(convertObjectErrorToErrorData(objectError));
        }
        return errorDatas;
    }

    private static ErrorData convertObjectErrorToErrorData(ObjectError objectError) {
        if (objectError instanceof FieldError) {
            FieldError fieldError = (FieldError) objectError;
            return new ErrorData.ErrorDataBuilder().setMessage(fieldError.getDefaultMessage()).setCode(fieldError.getCode()).build();
        }
        return new ErrorData.ErrorDataBuilder().setMessage(objectError.getDefaultMessage()).setCode(objectError.getCode()).build();
    }

}
