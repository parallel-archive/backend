package hu.codeandsoda.osa.account.user.data;


public class UserPublishedDocumentAnnotationData {

    private String publishedDocumentUrl;

    private String annotation;

    public UserPublishedDocumentAnnotationData() {
    }

    private UserPublishedDocumentAnnotationData(UserPublishedDocumentAnnotationDataBuilder userPublishedDocumentAnnotationDataBuilder) {
        publishedDocumentUrl = userPublishedDocumentAnnotationDataBuilder.publishedDocumentUrl;
        annotation = userPublishedDocumentAnnotationDataBuilder.annotation;
    }

    public String getPublishedDocumentUrl() {
        return publishedDocumentUrl;
    }

    public void setPublishedDocumentUrl(String publishedDocumentUrl) {
        this.publishedDocumentUrl = publishedDocumentUrl;
    }

    public String getAnnotation() {
        return annotation;
    }

    public void setAnnotation(String annotation) {
        this.annotation = annotation;
    }

    public static class UserPublishedDocumentAnnotationDataBuilder {

        private String publishedDocumentUrl;

        private String annotation;

        public UserPublishedDocumentAnnotationDataBuilder setPublishedDocumentUrl(String publishedDocumentUrl) {
            this.publishedDocumentUrl = publishedDocumentUrl;
            return this;
        }

        public UserPublishedDocumentAnnotationDataBuilder setAnnotation(String annotation) {
            this.annotation = annotation;
            return this;
        }

        public UserPublishedDocumentAnnotationData build() {
            return new UserPublishedDocumentAnnotationData(this);
        }

    }

}
