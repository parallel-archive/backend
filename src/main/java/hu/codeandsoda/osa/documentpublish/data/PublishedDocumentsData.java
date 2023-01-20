package hu.codeandsoda.osa.documentpublish.data;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.util.ObjectUtils;

import hu.codeandsoda.osa.general.data.ResponseData;
import hu.codeandsoda.osa.general.data.ResponseMessage;

public class PublishedDocumentsData extends ResponseData {

    private Page<PublishedDocumentData> documents;

    public PublishedDocumentsData() {
        documents = Page.empty();
    }

    private PublishedDocumentsData(PublishedDocumentsDataBuilder publishedDocumentsDataBuilder) {
        super(publishedDocumentsDataBuilder.messages);
        documents = publishedDocumentsDataBuilder.documents;
    }

    public Page<PublishedDocumentData> getDocuments() {
        return documents;
    }

    public void setDocuments(Page<PublishedDocumentData> documents) {
        this.documents = documents;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof PublishedDocumentsData) {
            PublishedDocumentsData d = (PublishedDocumentsData) o;
            return ObjectUtils.nullSafeEquals(o, d);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return documents.hashCode();
    }

    public static class PublishedDocumentsDataBuilder {

        private Page<PublishedDocumentData> documents;

        private List<ResponseMessage> messages;

        public PublishedDocumentsDataBuilder() {
            documents = Page.empty();
            messages = new ArrayList<>();
        }

        public PublishedDocumentsDataBuilder setDocuments(Page<PublishedDocumentData> documents) {
            this.documents = documents;
            return this;
        }

        public PublishedDocumentsDataBuilder setMessages(List<ResponseMessage> messages) {
            this.messages = messages;
            return this;
        }

        public PublishedDocumentsData build() {
            return new PublishedDocumentsData(this);
        }
    }
}
