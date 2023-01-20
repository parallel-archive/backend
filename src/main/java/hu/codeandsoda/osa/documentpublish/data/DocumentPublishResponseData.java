package hu.codeandsoda.osa.documentpublish.data;


public class DocumentPublishResponseData {

    private DocumentPublishResult result;

    public DocumentPublishResponseData() {
    }

    private DocumentPublishResponseData(DocumentPublishResponseDataBuilder documentPublishResponseDataBuilder) {
        result = documentPublishResponseDataBuilder.result;
    }

    public DocumentPublishResult getResult() {
        return result;
    }

    public void setResult(DocumentPublishResult result) {
        this.result = result;
    }

    public static class DocumentPublishResponseDataBuilder {

        private DocumentPublishResult result;

        public DocumentPublishResponseDataBuilder setResult(DocumentPublishResult result) {
            this.result = result;
            return this;
        }

        public DocumentPublishResponseData build() {
            return new DocumentPublishResponseData(this);
        }

    }

}
