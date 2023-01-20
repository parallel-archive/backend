package hu.codeandsoda.osa.myshoebox.data;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.util.ObjectUtils;

import com.fasterxml.jackson.annotation.JsonFormat;

import hu.codeandsoda.osa.general.data.ResponseData;
import hu.codeandsoda.osa.general.data.ResponseMessage;
import hu.codeandsoda.osa.util.OsaConstantUtil;

public class ImageData extends ResponseData {

    private Long id;

    private String name;

    private String url;

    private String activeUrl;

    private String thumbnailUrl;

    private String activeThumbnailUrl;

    private int rotation;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = OsaConstantUtil.DATE_JSON_FORMAT_PATTERN)
    private ZonedDateTime uploadedAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = OsaConstantUtil.DATE_JSON_FORMAT_PATTERN)
    private ZonedDateTime modifiedAt;

    public ImageData() {
    }

    private ImageData(ImageDataBuilder imageDataBuilder) {
        super(imageDataBuilder.messages);
        id = imageDataBuilder.id;
        name = imageDataBuilder.name;
        url = imageDataBuilder.url;
        activeUrl = imageDataBuilder.activeUrl;
        thumbnailUrl = imageDataBuilder.thumbnailUrl;
        activeThumbnailUrl = imageDataBuilder.activeThumbnailUrl;
        rotation = imageDataBuilder.rotation;
        uploadedAt = imageDataBuilder.uploadedAt;
        modifiedAt = imageDataBuilder.modifiedAt;
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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getActiveUrl() {
        return activeUrl;
    }

    public void setActiveUrl(String activeUrl) {
        this.activeUrl = activeUrl;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public String getActiveThumbnailUrl() {
        return activeThumbnailUrl;
    }

    public void setActiveThumbnailUrl(String activeThumbnailUrl) {
        this.activeThumbnailUrl = activeThumbnailUrl;
    }

    public int getRotation() {
        return rotation;
    }

    public void setRotation(int rotation) {
        this.rotation = rotation;
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
        if (o instanceof ImageData) {
            ImageData d = (ImageData) o;
            return ObjectUtils.nullSafeEquals(o, d);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return id.hashCode() ^ url.hashCode() ^ thumbnailUrl.hashCode() ^ uploadedAt.hashCode() ^ modifiedAt.hashCode();
    }

    public static class ImageDataBuilder {

        private Long id;

        private String name;

        private String url;

        private String activeUrl;

        private String thumbnailUrl;

        private String activeThumbnailUrl;

        private int rotation;

        private ZonedDateTime uploadedAt;

        private ZonedDateTime modifiedAt;

        private List<ResponseMessage> messages;

        public ImageDataBuilder() {
            messages = new ArrayList<>();
        }

        public ImageDataBuilder setId(Long id) {
            this.id = id;
            return this;
        }

        public ImageDataBuilder setName(String name) {
            this.name = name;
            return this;
        }

        public ImageDataBuilder setUrl(String url) {
            this.url = url;
            return this;
        }

        public ImageDataBuilder setActiveUrl(String activeUrl) {
            this.activeUrl = activeUrl;
            return this;
        }

        public ImageDataBuilder setThumbnailUrl(String thumbnailUrl) {
            this.thumbnailUrl = thumbnailUrl;
            return this;
        }

        public ImageDataBuilder setActiveThumbnailUrl(String activeThumbnailUrl) {
            this.activeThumbnailUrl = activeThumbnailUrl;
            return this;
        }

        public ImageDataBuilder setRotation(int rotation) {
            this.rotation = rotation;
            return this;
        }

        public ImageDataBuilder setUploadedAt(ZonedDateTime uploadedAt) {
            this.uploadedAt = uploadedAt;
            return this;
        }

        public ImageDataBuilder setModifiedAt(ZonedDateTime modifiedAt) {
            this.modifiedAt = modifiedAt;
            return this;
        }

        public ImageDataBuilder setMessages(List<ResponseMessage> messages) {
            this.messages = messages;
            return this;
        }

        public ImageData build() {
            return new ImageData(this);
        }

    }

}
