package hu.codeandsoda.osa.account.user.data;

import java.time.ZonedDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import hu.codeandsoda.osa.util.OsaConstantUtil;

public class UserImageData {

    private String name;

    private String url;

    private String thumbnailUrl;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = OsaConstantUtil.DATE_JSON_FORMAT_PATTERN)
    private ZonedDateTime uploadedAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = OsaConstantUtil.DATE_JSON_FORMAT_PATTERN)
    private ZonedDateTime modifiedAt;

    public UserImageData() {
    }

    private UserImageData(UserImageDataBuilder userImageDataBuilder) {
        name = userImageDataBuilder.name;
        url = userImageDataBuilder.url;
        thumbnailUrl = userImageDataBuilder.thumbnailUrl;
        uploadedAt = userImageDataBuilder.uploadedAt;
        modifiedAt = userImageDataBuilder.modifiedAt;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public ZonedDateTime getModifiedAt() {
        return modifiedAt;
    }

    public void setModifiedAt(ZonedDateTime modifiedAt) {
        this.modifiedAt = modifiedAt;
    }

    public static class UserImageDataBuilder {

        private String name;

        private String url;

        private String thumbnailUrl;

        private ZonedDateTime uploadedAt;

        private ZonedDateTime modifiedAt;

        public UserImageDataBuilder setName(String name) {
            this.name = name;
            return this;
        }

        public UserImageDataBuilder setUrl(String url) {
            this.url = url;
            return this;
        }

        public UserImageDataBuilder setThumbnailUrl(String thumbnailUrl) {
            this.thumbnailUrl = thumbnailUrl;
            return this;
        }

        public UserImageDataBuilder setUploadedAt(ZonedDateTime uploadedAt) {
            this.uploadedAt = uploadedAt;
            return this;
        }

        public UserImageDataBuilder setModifiedAt(ZonedDateTime modifiedAt) {
            this.modifiedAt = modifiedAt;
            return this;
        }

        public UserImageData build() {
            return new UserImageData(this);
        }

    }

}
