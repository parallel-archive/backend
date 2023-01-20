package hu.codeandsoda.osa.account.user.data;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import hu.codeandsoda.osa.util.OsaConstantUtil;

public class UserDocumentData {

    private UserDocumentMetaDataData metaData;

    private List<UserDocumentImageData> images;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = OsaConstantUtil.DATE_JSON_FORMAT_PATTERN)
    private ZonedDateTime uploadedAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = OsaConstantUtil.DATE_JSON_FORMAT_PATTERN)
    private ZonedDateTime modifiedAt;

    public UserDocumentData() {
        images = new ArrayList<>();
    }

    public UserDocumentData(UserDocumentDataBuilder userDocumentDataBuilder) {
        metaData = userDocumentDataBuilder.metaData;
        images = userDocumentDataBuilder.images;
        uploadedAt = userDocumentDataBuilder.uploadedAt;
        modifiedAt = userDocumentDataBuilder.modifiedAt;
    }

    public UserDocumentMetaDataData getMetaData() {
        return metaData;
    }

    public void setMetaData(UserDocumentMetaDataData metaData) {
        this.metaData = metaData;
    }

    public List<UserDocumentImageData> getImages() {
        return images;
    }

    public void setImages(List<UserDocumentImageData> images) {
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

    public static class UserDocumentDataBuilder {

        private UserDocumentMetaDataData metaData;

        private List<UserDocumentImageData> images;

        private ZonedDateTime uploadedAt;

        private ZonedDateTime modifiedAt;

        public UserDocumentDataBuilder() {
            images = new ArrayList<>();
        }

        public UserDocumentDataBuilder setMetaData(UserDocumentMetaDataData metaData) {
            this.metaData = metaData;
            return this;
        }

        public UserDocumentDataBuilder setImages(List<UserDocumentImageData> images) {
            this.images = images;
            return this;
        }

        public UserDocumentDataBuilder setUploadedAt(ZonedDateTime uploadedAt) {
            this.uploadedAt = uploadedAt;
            return this;
        }

        public UserDocumentDataBuilder setModifiedAt(ZonedDateTime modifiedAt) {
            this.modifiedAt = modifiedAt;
            return this;
        }

        public UserDocumentData build() {
            return new UserDocumentData(this);
        }

    }

}
