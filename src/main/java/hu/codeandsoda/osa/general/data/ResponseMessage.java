package hu.codeandsoda.osa.general.data;

public class ResponseMessage {

    private ResponseId id;

    private String message;

    private ResponseMessageScope responseMessageScope;

    public ResponseMessage() {
    }

    private ResponseMessage(ResponseMessageBuilder responseMessageBuilder) {
        id = responseMessageBuilder.id;
        message = responseMessageBuilder.message;
        responseMessageScope = responseMessageBuilder.responseMessageScope;
    }

    public ResponseId getId() {
        return id;
    }

    public void setId(ResponseId id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ResponseMessageScope getResponseMessageScope() {
        return responseMessageScope;
    }

    public void setResponseMessageScope(ResponseMessageScope responseMessageScope) {
        this.responseMessageScope = responseMessageScope;
    }

    public static class ResponseMessageBuilder {

        private ResponseId id;

        private String message;

        private ResponseMessageScope responseMessageScope;

        public ResponseMessageBuilder() {
        }

        public ResponseMessageBuilder setId(ResponseId id) {
            this.id = id;
            return this;
        }

        public ResponseMessageBuilder setMessage(String message) {
            this.message = message;
            return this;
        }

        public ResponseMessageBuilder setResponseMessageScope(ResponseMessageScope responseMessageScope) {
            this.responseMessageScope = responseMessageScope;
            return this;
        }

        public ResponseMessage build() {
            return new ResponseMessage(this);
        }
    }
}
