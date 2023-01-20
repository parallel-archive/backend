package hu.codeandsoda.osa.documentpublish.data;


public class DocumentPublishProgressResponse {

    private boolean publishInProgress;

    public DocumentPublishProgressResponse() {
    }

    private DocumentPublishProgressResponse(DocumentPublishProgressResponseBuilder documentPublishProgressResponseBuilder) {
        this.publishInProgress = documentPublishProgressResponseBuilder.publishInProgress;
    }

    public boolean isPublishInProgress() {
        return publishInProgress;
    }

    public void setPublishInProgress(boolean publishInProgress) {
        this.publishInProgress = publishInProgress;
    }

    public static class DocumentPublishProgressResponseBuilder {

        private boolean publishInProgress;

        public DocumentPublishProgressResponseBuilder setPublishInProgress(boolean publishInProgress) {
            this.publishInProgress = publishInProgress;
            return this;
        }

        public DocumentPublishProgressResponse build() {
            return new DocumentPublishProgressResponse(this);
        }

    }

}
