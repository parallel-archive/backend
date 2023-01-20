package hu.codeandsoda.osa.documentpublish.domain;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.validation.constraints.NotNull;

import hu.codeandsoda.osa.account.user.domain.User;
import hu.codeandsoda.osa.documentpublish.data.PublishedDocumentStatus;

@Entity
public class PublishedDocument implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String hash;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    @JoinColumn(foreignKey = @ForeignKey(name = "FK_PUBLISHED_DOCUMENT_META_DATA_ID"))
    @NotNull
    private PublishedDocumentMetaData publishedDocumentMetaData;

    private String pdfUrl;

    private String thumbnailUrl;

    @NotNull
    private ZonedDateTime createdAt;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    @NotNull
    private User user;

    @NotNull
    private int views;

    @OneToMany(mappedBy = "publishedDocument", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    @OrderBy("index ASC")
    private List<PublishedDocumentOcr> publishedDocumentOcrs;

    private String ipfsContentId;

    @Enumerated(EnumType.STRING)
    @NotNull
    private PublishedDocumentStatus status;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public PublishedDocumentMetaData getPublishedDocumentMetaData() {
        return publishedDocumentMetaData;
    }

    public void setPublishedDocumentMetaData(PublishedDocumentMetaData publishedDocumentMetaData) {
        this.publishedDocumentMetaData = publishedDocumentMetaData;
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

    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(ZonedDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getViews() {
        return views;
    }

    public void setViews(int views) {
        this.views = views;
    }

    public List<PublishedDocumentOcr> getPublishedDocumentOcrs() {
        return publishedDocumentOcrs;
    }

    public void setPublishedDocumentOcrs(List<PublishedDocumentOcr> publishedDocumentOcrs) {
        this.publishedDocumentOcrs = publishedDocumentOcrs;
    }

    public String getIpfsContentId() {
        return ipfsContentId;
    }

    public void setIpfsContentId(String ipfsContentId) {
        this.ipfsContentId = ipfsContentId;
    }

    public PublishedDocumentStatus getStatus() {
        return status;
    }

    public void setStatus(PublishedDocumentStatus status) {
        this.status = status;
    }

}
