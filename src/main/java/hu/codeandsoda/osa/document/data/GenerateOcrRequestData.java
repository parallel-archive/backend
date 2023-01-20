package hu.codeandsoda.osa.document.data;


public class GenerateOcrRequestData {

    private Long documentId;

    private Long publishedDocumentId;

    public GenerateOcrRequestData() {
    }

    private GenerateOcrRequestData(GenerateOcrRequestDataBuilder generateOcrRequestDataBuilder) {
        documentId = generateOcrRequestDataBuilder.documentId;
        publishedDocumentId = generateOcrRequestDataBuilder.publishedDocumentId;
    }

    public Long getDocumentId() {
        return documentId;
    }

    public void setDocumentId(Long documentId) {
        this.documentId = documentId;
    }

    public Long getPublishedDocumentId() {
        return publishedDocumentId;
    }

    public void setPublishedDocumentId(Long publishedDocumentId) {
        this.publishedDocumentId = publishedDocumentId;
    }

    public static class GenerateOcrRequestDataBuilder {

        private Long documentId;

        private Long publishedDocumentId;

        public GenerateOcrRequestDataBuilder setDocumentId(Long documentId) {
            this.documentId = documentId;
            return this;
        }

        public GenerateOcrRequestDataBuilder setPublishedDocumentId(Long publishedDocumentId) {
            this.publishedDocumentId = publishedDocumentId;
            return this;
        }

        public GenerateOcrRequestData build() {
            return new GenerateOcrRequestData(this);
        }

    }

}
