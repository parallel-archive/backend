package hu.codeandsoda.osa.documentpublish.data;

import hu.codeandsoda.osa.documentfilter.data.DocumentFilterTypesData;

public class PublicArchivePageData {

    private PublishedDocumentsData publishedDocuments;

    private DocumentFilterTypesData documentFilterTypesData;

    public PublicArchivePageData() {
    }

    private PublicArchivePageData(PublicArchivePageDataBuilder publicArchivePageDataBuilder) {
        this.publishedDocuments = publicArchivePageDataBuilder.publishedDocuments;
        this.documentFilterTypesData = publicArchivePageDataBuilder.documentFilterTypesData;
    }

    public PublishedDocumentsData getPublishedDocuments() {
        return publishedDocuments;
    }

    public void setPublishedDocuments(PublishedDocumentsData publishedDocuments) {
        this.publishedDocuments = publishedDocuments;
    }

    public DocumentFilterTypesData getDocumentFilterTypesData() {
        return documentFilterTypesData;
    }

    public void setDocumentFilterTypesData(DocumentFilterTypesData documentFilterTypesData) {
        this.documentFilterTypesData = documentFilterTypesData;
    }

    public static class PublicArchivePageDataBuilder {

        private PublishedDocumentsData publishedDocuments;

        private DocumentFilterTypesData documentFilterTypesData;

        public PublicArchivePageDataBuilder setPublishedDocuments(PublishedDocumentsData publishedDocuments) {
            this.publishedDocuments = publishedDocuments;
            return this;
        }

        public PublicArchivePageDataBuilder setDocumentFilterTypesData(DocumentFilterTypesData documentFilterTypesData) {
            this.documentFilterTypesData = documentFilterTypesData;
            return this;
        }

        public PublicArchivePageData build() {
            return new PublicArchivePageData(this);
        }

    }


}
