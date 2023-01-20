package hu.codeandsoda.osa.document.domain;

import java.io.Serializable;
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

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import hu.codeandsoda.osa.documentfilter.domain.DocumentFilter;

@Entity
public class DocumentMetaData implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String originalTitle;

    private String originalAuthor;

    private Integer createdAtYear;

    private Integer periodCoveredFrom;

    private Integer periodCoveredTo;

    private String archiveName;

    private String archiveCategory;

    private String publication;

    private String catalogUrl;

    private String sourceUrl;

    private String referenceCode;

    @ManyToMany(fetch = FetchType.EAGER)
    @Fetch(value = FetchMode.SUBSELECT)
    @JoinTable(name = "document_tag_document_meta_data", joinColumns = @JoinColumn(name = "document_meta_data_id", foreignKey = @ForeignKey(name = "FK_DOCUMENT_META_DATA_OF_TAG_ID")), inverseJoinColumns = @JoinColumn(name = "document_tag_id", foreignKey = @ForeignKey(name = "FK_DOCUMENT_TAG_ID")))
    private List<DocumentTag> tags;

    @ManyToMany(fetch = FetchType.EAGER)
    @Fetch(value = FetchMode.SUBSELECT)
    @JoinTable(name = "document_meta_data_document_filter", joinColumns = @JoinColumn(name = "document_meta_data_id", foreignKey = @ForeignKey(name = "FK_DOCUMENT_META_DATA_OF_DOCUMENT_FILTERS_ID")), inverseJoinColumns = @JoinColumn(name = "document_filter_id", foreignKey = @ForeignKey(name = "FK_DOCUMENT_FILTER_ID")))
    private List<DocumentFilter> documentFilters;

    public DocumentMetaData() {
        tags = new ArrayList<>();
        documentFilters = new ArrayList<>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public void setOriginalTitle(String originalTitle) {
        this.originalTitle = originalTitle;
    }

    public String getOriginalAuthor() {
        return originalAuthor;
    }

    public void setOriginalAuthor(String originalAuthor) {
        this.originalAuthor = originalAuthor;
    }

    public Integer getCreatedAtYear() {
        return createdAtYear;
    }

    public void setCreatedAtYear(Integer createdAtYear) {
        this.createdAtYear = createdAtYear;
    }

    public Integer getPeriodCoveredFrom() {
        return periodCoveredFrom;
    }

    public void setPeriodCoveredFrom(Integer periodCoveredFrom) {
        this.periodCoveredFrom = periodCoveredFrom;
    }

    public Integer getPeriodCoveredTo() {
        return periodCoveredTo;
    }

    public void setPeriodCoveredTo(Integer periodCoveredTo) {
        this.periodCoveredTo = periodCoveredTo;
    }

    public String getArchiveName() {
        return archiveName;
    }

    public void setArchiveName(String archiveName) {
        this.archiveName = archiveName;
    }

    public String getArchiveCategory() {
        return archiveCategory;
    }

    public void setArchiveCategory(String archiveCategory) {
        this.archiveCategory = archiveCategory;
    }

    public String getPublication() {
        return publication;
    }

    public void setPublication(String publication) {
        this.publication = publication;
    }

    public String getCatalogUrl() {
        return catalogUrl;
    }

    public void setCatalogUrl(String catalogUrl) {
        this.catalogUrl = catalogUrl;
    }

    public String getSourceUrl() {
        return sourceUrl;
    }

    public void setSourceUrl(String sourceUrl) {
        this.sourceUrl = sourceUrl;
    }

    public String getReferenceCode() {
        return referenceCode;
    }

    public void setReferenceCode(String referenceCode) {
        this.referenceCode = referenceCode;
    }

    public List<DocumentTag> getTags() {
        return tags;
    }

    public void setTags(List<DocumentTag> tags) {
        this.tags = tags;
    }

    public List<DocumentFilter> getDocumentFilters() {
        return documentFilters;
    }

    public void setDocumentFilters(List<DocumentFilter> documentFilters) {
        this.documentFilters = documentFilters;
    }

}
