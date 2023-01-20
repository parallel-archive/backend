package hu.codeandsoda.osa.documentfilter.domain;

import java.io.Serializable;

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
import javax.validation.constraints.NotNull;

import hu.codeandsoda.osa.documentfilter.data.DocumentFilterName;

@Entity
public class DocumentFilter implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private DocumentFilterName name;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(foreignKey = @ForeignKey(name = "FK_DOCUMENT_FILTER_DOCUMENT_FILTER_TYPE_ID"))
    @NotNull
    private DocumentFilterType documentFilterType;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public DocumentFilterName getName() {
        return name;
    }

    public void setName(DocumentFilterName name) {
        this.name = name;
    }

    public DocumentFilterType getDocumentFilterType() {
        return documentFilterType;
    }

    public void setDocumentFilterType(DocumentFilterType documentFilterType) {
        this.documentFilterType = documentFilterType;
    }

}
