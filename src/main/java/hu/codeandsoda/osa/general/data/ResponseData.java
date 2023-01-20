package hu.codeandsoda.osa.general.data;

import java.util.ArrayList;
import java.util.List;

import org.springframework.util.ObjectUtils;

public class ResponseData {

    private List<ResponseMessage> messages;

    public ResponseData() {
        messages = new ArrayList<>();
    }

    public ResponseData(List<ResponseMessage> messages) {
        this.messages = messages;
    }

    private ResponseData(ResponseDataBuilder responseDataBuilder) {
        messages = responseDataBuilder.messages;
    }

    public List<ResponseMessage> getMessages() {
        return messages;
    }

    public void setMessages(List<ResponseMessage> messages) {
        this.messages = messages;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof ResponseData) {
            ResponseData d = (ResponseData) o;
            return ObjectUtils.nullSafeEquals(o, d);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return messages.hashCode();
    }

    public static class ResponseDataBuilder {

        private List<ResponseMessage> messages;

        public ResponseDataBuilder() {
            messages = new ArrayList<>();
        }

        public ResponseDataBuilder setMessages(List<ResponseMessage> messages) {
            this.messages = messages;
            return this;
        }

        public ResponseData build() {
            return new ResponseData(this);
        }
    }

}
