package hu.codeandsoda.osa.document.domain;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
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

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import hu.codeandsoda.osa.account.user.domain.User;
import hu.codeandsoda.osa.documentpublish.domain.PublishedDocument;

@Entity
public class Document implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    @JoinColumn(foreignKey = @ForeignKey(name = "FK_DOCUMENT_META_DATA_ID"))
    @NotNull
    private DocumentMetaData metaData;

    @OneToMany(mappedBy = "document", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    @Fetch(value = FetchMode.SUBSELECT)
    @OrderBy("index ASC")
    private List<DocumentImage> images;

    @NotNull
    private ZonedDateTime uploadedAt;

    @NotNull
    private ZonedDateTime modifiedAt;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    @NotNull
    private User user;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(foreignKey = @ForeignKey(name = "FK_DOCUMENT_PUBLISHED_DOCUMENT_ID"))
    private PublishedDocument publishedDocument;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    
    public DocumentMetaData getMetaData() {
        return metaData;
    }
    
    public void setMetaData(DocumentMetaData metaData) {
        this.metaData = metaData;
    }

    public List<DocumentImage> getImages() {
        return images;
    }

    public void setImages(List<DocumentImage> images) {
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public PublishedDocument getPublishedDocument() {
        return publishedDocument;
    }

    public void setPublishedDocument(PublishedDocument publishedDocument) {
        this.publishedDocument = publishedDocument;
    }

}
