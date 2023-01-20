package hu.codeandsoda.osa.collection.data;

public class CollectionContentRequestData {

    private Long collectionId;

    private String publishedDocumentHash;

    public CollectionContentRequestData() {
    }

    private CollectionContentRequestData(CollectionContentRequestDataBuilder collectionContentRequestDataBuilder) {
        collectionId = collectionContentRequestDataBuilder.collectionId;
        publishedDocumentHash = collectionContentRequestDataBuilder.publishedDocumentHash;
    }

    public Long getCollectionId() {
        return collectionId;
    }

    public void setCollectionId(Long collectionId) {
        this.collectionId = collectionId;
    }

    public String getPublishedDocumentHash() {
        return publishedDocumentHash;
    }

    public void setPublishedDocumentHash(String publishedDocumentHash) {
        this.publishedDocumentHash = publishedDocumentHash;
    }

    public static class CollectionContentRequestDataBuilder {

        private Long collectionId;

        private String publishedDocumentHash;

        public CollectionContentRequestDataBuilder setCollectionId(Long collectionId) {
            this.collectionId = collectionId;
            return this;
        }

        public CollectionContentRequestDataBuilder setPublishedDocumentHash(String publishedDocumentHash) {
            this.publishedDocumentHash = publishedDocumentHash;
            return this;
        }

        public CollectionContentRequestData build() {
            return new CollectionContentRequestData(this);
        }

    }

}
