package hu.codeandsoda.osa.exception;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import hu.codeandsoda.osa.general.data.ErrorData;
import hu.codeandsoda.osa.general.data.ErrorMessage;

@ControllerAdvice
@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class ControllerExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    @ResponseBody
    ErrorMessage handleException(BusinessException ex) {
        List<ErrorData> errors = ObjectErrorToErrorDataConverter.convertObjectErrorsToErrorDatas(ex.getErrors());
        return new ErrorMessage(errors);
    }

}
