package hu.codeandsoda.osa.document.data;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.util.ObjectUtils;

import com.fasterxml.jackson.annotation.JsonFormat;

import hu.codeandsoda.osa.general.data.ResponseData;
import hu.codeandsoda.osa.general.data.ResponseMessage;
import hu.codeandsoda.osa.util.OsaConstantUtil;

public class DocumentImageData extends ResponseData {

    private Long id;

    private String name;

    private int index;

    private String url;

    private String thumbnailUrl;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = OsaConstantUtil.DATE_JSON_FORMAT_PATTERN)
    private ZonedDateTime uploadedAt;

    // Edited ocr
    private String ocr;

    public DocumentImageData() {
    }

    private DocumentImageData(DocumentImageDataBuilder documentImageDataBuilder) {
        super(documentImageDataBuilder.messages);
        id = documentImageDataBuilder.id;
        name = documentImageDataBuilder.name;
        index = documentImageDataBuilder.index;
        url = documentImageDataBuilder.url;
        thumbnailUrl = documentImageDataBuilder.thumbnailUrl;
        uploadedAt = documentImageDataBuilder.uploadedAt;
        ocr = documentImageDataBuilder.ocr;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public ZonedDateTime getUploadedAt() {
        return uploadedAt;
    }

    public void setUploadedAt(ZonedDateTime uploadedAt) {
        this.uploadedAt = uploadedAt;
    }

    public String getOcr() {
        return ocr;
    }

    public void setOcr(String ocr) {
        this.ocr = ocr;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof DocumentImageData) {
            DocumentImageData d = (DocumentImageData) o;
            return ObjectUtils.nullSafeEquals(o, d);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return id.hashCode() ^ name.hashCode() ^ url.hashCode() ^ thumbnailUrl.hashCode() ^ uploadedAt.hashCode() ^ ocr.hashCode();
    }

    public static class DocumentImageDataBuilder {

        private Long id;

        private String name;

        private int index;

        private String url;

        private String thumbnailUrl;

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = OsaConstantUtil.DATE_JSON_FORMAT_PATTERN)
        private ZonedDateTime uploadedAt;

        private String ocr;

        private List<ResponseMessage> messages;

        public DocumentImageDataBuilder() {
            messages = new ArrayList<>();
        }

        public DocumentImageDataBuilder setId(Long id) {
            this.id = id;
            return this;
        }

        public DocumentImageDataBuilder setName(String name) {
            this.name = name;
            return this;
        }

        public DocumentImageDataBuilder setIndex(int index) {
            this.index = index;
            return this;
        }

        public DocumentImageDataBuilder setUrl(String url) {
            this.url = url;
            return this;
        }

        public DocumentImageDataBuilder setThumbnailUrl(String thumbnailUrl) {
            this.thumbnailUrl = thumbnailUrl;
            return this;
        }

        public DocumentImageDataBuilder setUploadedAt(ZonedDateTime uploadedAt) {
            this.uploadedAt = uploadedAt;
            return this;
        }

        public DocumentImageDataBuilder setMessages(List<ResponseMessage> messages) {
            this.messages = messages;
            return this;
        }

        public DocumentImageDataBuilder setOcr(String ocr) {
            this.ocr = ocr;
            return this;
        }

        public DocumentImageData build() {
            return new DocumentImageData(this);
        }

    }

}
