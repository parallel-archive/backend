package hu.codeandsoda.osa.document.data;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.util.ObjectUtils;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import hu.codeandsoda.osa.general.data.ResponseData;
import hu.codeandsoda.osa.general.data.ResponseMessage;
import hu.codeandsoda.osa.util.OsaConstantUtil;

public class DocumentData extends ResponseData {

    private Long id;

    @JsonIgnoreProperties({ "messages" })
    private DocumentMetaDataData metaData;

    @JsonIgnoreProperties({ "messages" })
    private List<DocumentImageData> images;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = OsaConstantUtil.DATE_JSON_FORMAT_PATTERN)
    private ZonedDateTime uploadedAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = OsaConstantUtil.DATE_JSON_FORMAT_PATTERN)
    private ZonedDateTime modifiedAt;

    public DocumentData() {
    }

    private DocumentData(DocumentDataBuilder documentDataBuilder) {
        super(documentDataBuilder.messages);
        id = documentDataBuilder.id;
        metaData = documentDataBuilder.metaData;
        images = documentDataBuilder.images;
        uploadedAt = documentDataBuilder.uploadedAt;
        modifiedAt = documentDataBuilder.modifiedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public DocumentMetaDataData getMetaData() {
        return metaData;
    }

    public void setMetaData(DocumentMetaDataData metaData) {
        this.metaData = metaData;
    }

    public List<DocumentImageData> getImages() {
        return images;
    }

    public void setImages(List<DocumentImageData> images) {
        this.images = images;
    }

    public ZonedDateTime getUploadedAt() {
        return uploadedAt;
    }

    public void setUploadedAt(ZonedDateTime uploadedAt) {
        this.uploadedAt = uploadedAt;
    }

    public ZonedDateTime getModifiedAt() {
        return modifiedAt;
    }

    public void setModifiedAt(ZonedDateTime modifiedAt) {
        this.modifiedAt = modifiedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof DocumentData) {
            DocumentData d = (DocumentData) o;
            return ObjectUtils.nullSafeEquals(o, d);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return id.hashCode() ^ metaData.hashCode() ^ images.hashCode() ^ uploadedAt.hashCode() ^ modifiedAt.hashCode();
    }

    public static class DocumentDataBuilder {

        private Long id;

        private DocumentMetaDataData metaData;

        private List<DocumentImageData> images;

        private ZonedDateTime uploadedAt;

        private ZonedDateTime modifiedAt;

        private List<ResponseMessage> messages;

        public DocumentDataBuilder() {
            images = new ArrayList<>();
            messages = new ArrayList<>();
        }

        public DocumentDataBuilder setId(Long id) {
            this.id = id;
            return this;
        }

        public DocumentDataBuilder setMetaData(DocumentMetaDataData metaData) {
            this.metaData = metaData;
            return this;
        }

        public DocumentDataBuilder setImages(List<DocumentImageData> images) {
            this.images = images;
            return this;
        }


        public DocumentDataBuilder setUploadedAt(ZonedDateTime uploadedAt) {
            this.uploadedAt = uploadedAt;
            return this;
        }

        public DocumentDataBuilder setModifiedAt(ZonedDateTime modifiedAt) {
            this.modifiedAt = modifiedAt;
            return this;
        }

        public DocumentDataBuilder setMessages(List<ResponseMessage> messages) {
            this.messages = messages;
            return this;
        }

        public DocumentData build() {
            return new DocumentData(this);
        }

    }

}
