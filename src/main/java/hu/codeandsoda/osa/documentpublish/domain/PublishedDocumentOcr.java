package hu.codeandsoda.osa.documentpublish.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

@Entity
public class PublishedDocumentOcr implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "published_document_id")
    @NotNull
    private PublishedDocument publishedDocument;

    @Column(name = "page_index")
    private int index;

    private String originalOcr;

    private String editedOcr;

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

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
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

}
