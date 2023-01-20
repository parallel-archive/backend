package hu.codeandsoda.osa.collection.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OrderBy;
import javax.validation.constraints.NotNull;

import hu.codeandsoda.osa.account.user.domain.User;
import hu.codeandsoda.osa.documentpublish.domain.PublishedDocument;

@Entity
public class Collection {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(foreignKey = @ForeignKey(name = "FK_COLLECTION_USER_ID"))
    @NotNull
    private User user;

    @ManyToMany
    @OrderBy("createdAt DESC")
    @JoinTable(name = "collection_published_document", joinColumns = @JoinColumn(name = "collection_id", foreignKey = @ForeignKey(name = "FK_COLLECTION_OF_PUBLISHED_DOCUMENTS_ID")), inverseJoinColumns = @JoinColumn(name = "published_document_id", foreignKey = @ForeignKey(name = "FK_COLLECTION_PUBLISHED_DOCUMENTS_ID")))
    private List<PublishedDocument> documents;

    public Collection() {
        documents = new ArrayList<>();
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<PublishedDocument> getDocuments() {
        return documents;
    }

    public void setDocuments(List<PublishedDocument> documents) {
        this.documents = documents;
    }

}
