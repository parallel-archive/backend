package hu.codeandsoda.osa.general.data;

import java.util.List;

public class ErrorMessage {

    private List<ErrorData> errors;

    public ErrorMessage() {
    }

    public ErrorMessage(List<ErrorData> errors) {
        super();
        this.errors = errors;
    }

    public List<ErrorData> getErrors() {
        return errors;
    }

    public void setErrors(List<ErrorData> errors) {
        this.errors = errors;
    }

}
