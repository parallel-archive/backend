package hu.codeandsoda.osa.documentfilter.domain;

import java.io.Serializable;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;

import hu.codeandsoda.osa.documentfilter.data.DocumentFilterTypeName;

@Entity
public class DocumentFilterType implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private DocumentFilterTypeName name;

    @OneToMany(mappedBy = "documentFilterType", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("name ASC")
    private List<DocumentFilter> documentFilters;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public DocumentFilterTypeName getName() {
        return name;
    }

    public void setName(DocumentFilterTypeName name) {
        this.name = name;
    }

    public List<DocumentFilter> getDocumentFilters() {
        return documentFilters;
    }

    public void setDocumentFilters(List<DocumentFilter> documentFilters) {
        this.documentFilters = documentFilters;
    }

}
