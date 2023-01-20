package hu.codeandsoda.osa.documentpublish.domain;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

import hu.codeandsoda.osa.account.user.domain.User;

@Entity
public class PublishedDocumentAnnotation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(foreignKey = @ForeignKey(name = "FK_ANNOTATION_PUBLISHED_DOCUMENT_ID"))
    @NotNull
    private PublishedDocument publishedDocument;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(foreignKey = @ForeignKey(name = "FK_ANNOTATION_USER_ID"))
    @NotNull
    private User user;

    private String annotation;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PublishedDocument getPublishedDocument() {
        return publishedDocument;
    }

    public void setPublishedDocument(PublishedDocument publishedDocument) {
        this.publishedDocument = publishedDocument;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getAnnotation() {
        return annotation;
    }

    public void setAnnotation(String annotation) {
        this.annotation = annotation;
    }

}
