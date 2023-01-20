package hu.codeandsoda.osa.account.user.data;

import java.time.ZonedDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import hu.codeandsoda.osa.util.OsaConstantUtil;

public class UserDocumentImageData {

    private String name;

    private int index;

    private String url;

    private String thumbnailUrl;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = OsaConstantUtil.DATE_JSON_FORMAT_PATTERN)
    private ZonedDateTime uploadedAt;

    private String originalOcr;

    private String editedOcr;

    private UserDocumentImageData(UserDocumentImageDataBuilder userDocumentImageDataBuilder) {
        name = userDocumentImageDataBuilder.name;
        index = userDocumentImageDataBuilder.index;
        url = userDocumentImageDataBuilder.url;
        thumbnailUrl = userDocumentImageDataBuilder.thumbnailUrl;
        uploadedAt = userDocumentImageDataBuilder.uploadedAt;
        originalOcr = userDocumentImageDataBuilder.originalOcr;
        editedOcr = userDocumentImageDataBuilder.editedOcr;
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

    public String getOriginalOcr() {
        return originalOcr;
    }

    public void setOriginalOcr(String originalOcr) {
        this.originalOcr = originalOcr;
    }

    public String getEditedOcr() {
        return editedOcr;
    }

    public void setEditedOcr(String editedOcr) {
        this.editedOcr = editedOcr;
    }

    public static class UserDocumentImageDataBuilder {

        private String name;

        private int index;

        private String url;

        private String thumbnailUrl;

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = OsaConstantUtil.DATE_JSON_FORMAT_PATTERN)
        private ZonedDateTime uploadedAt;

        private String originalOcr;

        private String editedOcr;

        public UserDocumentImageDataBuilder setName(String name) {
            this.name = name;
            return this;
        }

        public UserDocumentImageDataBuilder setIndex(int index) {
            this.index = index;
            return this;
        }

        public UserDocumentImageDataBuilder setUrl(String url) {
            this.url = url;
            return this;
        }

        public UserDocumentImageDataBuilder setThumbnailUrl(String thumbnailUrl) {
            this.thumbnailUrl = thumbnailUrl;
            return this;
        }

        public UserDocumentImageDataBuilder setUploadedAt(ZonedDateTime uploadedAt) {
            this.uploadedAt = uploadedAt;
            return this;
        }

        public UserDocumentImageDataBuilder setOriginalOcr(String originalOcr) {
            this.originalOcr = originalOcr;
            return this;
        }

        public UserDocumentImageDataBuilder setEditedOcr(String editedOcr) {
            this.editedOcr = editedOcr;
            return this;
        }

        public UserDocumentImageData build() {
            return new UserDocumentImageData(this);
        }

    }
}
