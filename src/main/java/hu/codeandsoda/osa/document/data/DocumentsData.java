package hu.codeandsoda.osa.document.data;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.util.ObjectUtils;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import hu.codeandsoda.osa.general.data.ResponseData;
import hu.codeandsoda.osa.general.data.ResponseMessage;

public class DocumentsData extends ResponseData {

    @JsonIgnoreProperties({ "content.messages" })
    private Page<DocumentData> documentDataPage;

    public DocumentsData() {
    }

    private DocumentsData(DocumentsDataBuilder documentsDataBuilder) {
        super(documentsDataBuilder.messages);
        documentDataPage = documentsDataBuilder.documentDataPage;
    }

    public Page<DocumentData> getDocumentDataPage() {
        return documentDataPage;
    }

    public void setDocumentDataPage(Page<DocumentData> documentDataPage) {
        this.documentDataPage = documentDataPage;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof DocumentData) {
            DocumentsData d = (DocumentsData) o;
            return ObjectUtils.nullSafeEquals(o, d);
        }
        return false;
    }

    public static class DocumentsDataBuilder {

        private Page<DocumentData> documentDataPage;

        private List<ResponseMessage> messages;

        public DocumentsDataBuilder() {
            documentDataPage = Page.empty();
            messages = new ArrayList<>();
        }

        public DocumentsDataBuilder setDocumentDataPage(Page<DocumentData> documentDataPage) {
            this.documentDataPage = documentDataPage;
            return this;
        }

        public DocumentsDataBuilder setMessages(List<ResponseMessage> messages) {
            this.messages = messages;
            return this;
        }

        public DocumentsData build() {
            return new DocumentsData(this);
        }
    }
}
