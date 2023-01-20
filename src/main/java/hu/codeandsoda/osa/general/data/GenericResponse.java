package hu.codeandsoda.osa.general.data;

import org.springframework.util.ObjectUtils;

public class GenericResponse {

    private ResultCode resultCode;

    public GenericResponse() {
    }

    private GenericResponse(GenericResponseBuilder genericResponseBuilder) {
        resultCode = genericResponseBuilder.resultCode;
    }

    public ResultCode getResultCode() {
        return resultCode;
    }

    public void setResultCode(ResultCode resultCode) {
        this.resultCode = resultCode;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof GenericResponse) {
            GenericResponse d = (GenericResponse) o;
            return ObjectUtils.nullSafeEquals(o, d);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return resultCode.hashCode();
    }

    public static class GenericResponseBuilder {

        private ResultCode resultCode;

        public GenericResponseBuilder() {
        }

        public GenericResponseBuilder setResultCode(ResultCode resultCode) {
            this.resultCode = resultCode;
            return this;
        }

        public GenericResponse build() {
            return new GenericResponse(this);
        }
    }

}
