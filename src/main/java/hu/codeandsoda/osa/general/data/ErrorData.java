package hu.codeandsoda.osa.general.data;

import java.util.Objects;

public class ErrorData {

    private String message;

    private String code;

    public ErrorData() {
    }

    public ErrorData(ErrorDataBuilder errorDataBuilder) {
        message = errorDataBuilder.message;
        code = errorDataBuilder.code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof ErrorData) {
            ErrorData d = (ErrorData) o;

            return message.equals(d.message) && code.equals(d.code);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(message, code);
    }

    public static class ErrorDataBuilder {

        private String message;

        private String code;

        public ErrorDataBuilder setMessage(String message) {
            this.message = message;
            return this;
        }

        public ErrorDataBuilder setCode(String code) {
            this.code = code;
            return this;
        }

        public ErrorData build() {
            return new ErrorData(this);
        }
    }

}
