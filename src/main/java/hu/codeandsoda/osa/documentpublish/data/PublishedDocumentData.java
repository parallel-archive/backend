package hu.codeandsoda.osa.documentpublish.data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.util.ObjectUtils;

import hu.codeandsoda.osa.general.data.ResponseData;
import hu.codeandsoda.osa.general.data.ResponseMessage;

public class PublishedDocumentData extends ResponseData {

    private String hash;

    private PublishedDocumentMetaDataData metaData;

    private String url;

    private String pdfUrl;

    private String thumbnailUrl;

    private Date createdAt;

    private String userName;

    private int views;

    private List<String> originalOcrs;

    private List<String> editedOcrs;

    private String annotation;

    private String ipfsContentId;

    public PublishedDocumentData() {
        originalOcrs = new ArrayList<>();
        editedOcrs = new ArrayList<>();
    }

    private PublishedDocumentData(PublishedDocumentDataBuilder publishedDocumentDataBuilder) {
        super(publishedDocumentDataBuilder.messages);
        metaData = publishedDocumentDataBuilder.metaData;
        url = publishedDocumentDataBuilder.url;
        pdfUrl = publishedDocumentDataBuilder.pdfUrl;
        thumbnailUrl = publishedDocumentDataBuilder.thumbnailUrl;
        createdAt = publishedDocumentDataBuilder.createdAt;
        userName = publishedDocumentDataBuilder.userName;
        views = publishedDocumentDataBuilder.views;
        hash = publishedDocumentDataBuilder.hash;
        originalOcrs = publishedDocumentDataBuilder.originalOcrs;
        editedOcrs = publishedDocumentDataBuilder.editedOcrs;
        annotation = publishedDocumentDataBuilder.annotation;
        ipfsContentId = publishedDocumentDataBuilder.ipfsContentId;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public PublishedDocumentMetaDataData getMetaData() {
        return metaData;
    }

    public void setMetaData(PublishedDocumentMetaDataData metaData) {
        this.metaData = metaData;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPdfUrl() {
        return pdfUrl;
    }

    public void setPdfUrl(String pdfUrl) {
        this.pdfUrl = pdfUrl;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getViews() {
        return views;
    }

    public void setViews(int views) {
        this.views = views;
    }

    public List<String> getOriginalOcrs() {
        return originalOcrs;
    }

    public void setOriginalOcrs(List<String> originalOcrs) {
        this.originalOcrs = originalOcrs;
    }

    public List<String> getEditedOcrs() {
        return editedOcrs;
    }

    public void setEditedOcrs(List<String> editedOcrs) {
        this.editedOcrs = editedOcrs;
    }

    public String getAnnotation() {
        return annotation;
    }

    public void setAnnotation(String annotation) {
        this.annotation = annotation;
    }

    public String getIpfsContentId() {
        return ipfsContentId;
    }

    public void setIpfsContentId(String ipfsContentId) {
        this.ipfsContentId = ipfsContentId;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof PublishedDocumentData) {
            PublishedDocumentData d = (PublishedDocumentData) o;
            return ObjectUtils.nullSafeEquals(o, d);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return metaData.hashCode() ^ url.hashCode() ^ pdfUrl.hashCode() ^ thumbnailUrl.hashCode() ^ createdAt.hashCode() ^ userName.hashCode() ^ hash.hashCode()
                ^ originalOcrs.hashCode() ^ editedOcrs.hashCode() ^ annotation.hashCode();
    }

    public static class PublishedDocumentDataBuilder {

        private String hash;

        private PublishedDocumentMetaDataData metaData;

        private String url;

        private String pdfUrl;

        private String thumbnailUrl;

        private Date createdAt;

        private String userName;

        private int views;

        private List<String> originalOcrs;

        private List<String> editedOcrs;

        private List<ResponseMessage> messages;

        private String annotation;

        private String ipfsContentId;

        public PublishedDocumentDataBuilder() {
            messages = new ArrayList<>();
            originalOcrs = new ArrayList<>();
            editedOcrs = new ArrayList<>();
        }

        public PublishedDocumentDataBuilder setHash(String hash) {
            this.hash = hash;
            return this;
        }

        public PublishedDocumentDataBuilder setMetaData(PublishedDocumentMetaDataData metaData) {
            this.metaData = metaData;
            return this;
        }

        public PublishedDocumentDataBuilder setUrl(String url) {
            this.url = url;
            return this;
        }

        public PublishedDocumentDataBuilder setPdfUrl(String pdfUrl) {
            this.pdfUrl = pdfUrl;
            return this;
        }

        public PublishedDocumentDataBuilder setThumbnailUrl(String thumbnailUrl) {
            this.thumbnailUrl = thumbnailUrl;
            return this;
        }

        public PublishedDocumentDataBuilder setCreatedAt(Date createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public PublishedDocumentDataBuilder setUserName(String userName) {
            this.userName = userName;
            return this;
        }

        public PublishedDocumentDataBuilder setViews(int views) {
            this.views = views;
            return this;
        }

        public PublishedDocumentDataBuilder setOriginalOcrs(List<String> originalOcrs) {
            this.originalOcrs = originalOcrs;
            return this;
        }

        public PublishedDocumentDataBuilder setEditedOcrs(List<String> editedOcrs) {
            this.editedOcrs = editedOcrs;
            return this;
        }

        public PublishedDocumentDataBuilder setAnnotation(String annotation) {
            this.annotation = annotation;
            return this;
        }

        public PublishedDocumentDataBuilder setMessages(List<ResponseMessage> messages) {
            this.messages = messages;
            return this;
        }

        public PublishedDocumentDataBuilder setIpfsContentId(String ipfsContentId) {
            this.ipfsContentId = ipfsContentId;
            return this;
        }

        public PublishedDocumentData build() {
            return new PublishedDocumentData(this);
        }

    }

}
