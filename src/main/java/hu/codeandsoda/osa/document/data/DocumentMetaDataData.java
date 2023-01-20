package hu.codeandsoda.osa.document.data;

import java.util.ArrayList;
import java.util.List;

import hu.codeandsoda.osa.documentfilter.data.DocumentFilterTypeData;
import hu.codeandsoda.osa.general.data.ResponseData;

public class DocumentMetaDataData extends ResponseData {

    private String originalTitle;

    private String originalAuthor;

    private Integer createdAtYear;

    private DocumentFilterTypeData types;

    private DocumentFilterTypeData languages;

    private Integer periodCoveredFrom;

    private Integer periodCoveredTo;

    private DocumentFilterTypeData countriesCovered;

    private String archiveName;

    private String archiveCategory;

    private String publication;

    private String catalogUrl;

    private String sourceUrl;

    private String referenceCode;

    private List<String> tags;

    public DocumentMetaDataData() {
        tags = new ArrayList<>();
    }

    private DocumentMetaDataData(DocumentMetaDataDataBuilder documentMetaDataDataBuilder) {
        originalTitle = documentMetaDataDataBuilder.originalTitle;
        originalAuthor = documentMetaDataDataBuilder.originalAuthor;
        createdAtYear = documentMetaDataDataBuilder.createdAtYear;
        types = documentMetaDataDataBuilder.types;
        languages = documentMetaDataDataBuilder.languages;
        periodCoveredFrom = documentMetaDataDataBuilder.periodCoveredFrom;
        periodCoveredTo = documentMetaDataDataBuilder.periodCoveredTo;
        countriesCovered = documentMetaDataDataBuilder.countriesCovered;
        archiveName = documentMetaDataDataBuilder.archiveName;
        archiveCategory = documentMetaDataDataBuilder.archiveCategory;
        publication = documentMetaDataDataBuilder.publication;
        catalogUrl = documentMetaDataDataBuilder.catalogUrl;
        sourceUrl = documentMetaDataDataBuilder.sourceUrl;
        referenceCode = documentMetaDataDataBuilder.referenceCode;
        tags = documentMetaDataDataBuilder.tags;
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

    public DocumentFilterTypeData getTypes() {
        return types;
    }

    public void setTypes(DocumentFilterTypeData types) {
        this.types = types;
    }

    public DocumentFilterTypeData getLanguages() {
        return languages;
    }

    public void setLanguages(DocumentFilterTypeData languages) {
        this.languages = languages;
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

    public DocumentFilterTypeData getCountriesCovered() {
        return countriesCovered;
    }

    public void setCountriesCovered(DocumentFilterTypeData countriesCovered) {
        this.countriesCovered = countriesCovered;
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

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public static class DocumentMetaDataDataBuilder {

        private String originalTitle;

        private String originalAuthor;

        private Integer createdAtYear;

        private DocumentFilterTypeData types;

        private DocumentFilterTypeData languages;

        private Integer periodCoveredFrom;

        private Integer periodCoveredTo;

        private DocumentFilterTypeData countriesCovered;

        private String archiveName;

        private String archiveCategory;

        private String publication;

        private String catalogUrl;

        private String sourceUrl;

        private String referenceCode;

        private List<String> tags;

        public DocumentMetaDataDataBuilder() {
            tags = new ArrayList<>();
        }

        public DocumentMetaDataDataBuilder setOriginalTitle(String originalTitle) {
            this.originalTitle = originalTitle;
            return this;
        }

        public DocumentMetaDataDataBuilder setOriginalAuthor(String originalAuthor) {
            this.originalAuthor = originalAuthor;
            return this;
        }

        public DocumentMetaDataDataBuilder setCreatedAtYear(Integer createdAtYear) {
            this.createdAtYear = createdAtYear;
            return this;
        }

        public DocumentMetaDataDataBuilder setTypes(DocumentFilterTypeData types) {
            this.types = types;
            return this;
        }

        public DocumentMetaDataDataBuilder setLanguages(DocumentFilterTypeData languages) {
            this.languages = languages;
            return this;
        }

        public DocumentMetaDataDataBuilder setPeriodCoveredFrom(Integer periodCoveredFrom) {
            this.periodCoveredFrom = periodCoveredFrom;
            return this;
        }

        public DocumentMetaDataDataBuilder setPeriodCoveredTo(Integer periodCoveredTo) {
            this.periodCoveredTo = periodCoveredTo;
            return this;
        }

        public DocumentMetaDataDataBuilder setCountriesCovered(DocumentFilterTypeData countriesCovered) {
            this.countriesCovered = countriesCovered;
            return this;
        }

        public DocumentMetaDataDataBuilder setArchiveName(String archiveName) {
            this.archiveName = archiveName;
            return this;
        }

        public DocumentMetaDataDataBuilder setArchiveCategory(String archiveCategory) {
            this.archiveCategory = archiveCategory;
            return this;
        }

        public DocumentMetaDataDataBuilder setPublication(String publication) {
            this.publication = publication;
            return this;
        }

        public DocumentMetaDataDataBuilder setCatalogUrl(String catalogUrl) {
            this.catalogUrl = catalogUrl;
            return this;
        }

        public DocumentMetaDataDataBuilder setSourceUrl(String sourceUrl) {
            this.sourceUrl = sourceUrl;
            return this;
        }

        public DocumentMetaDataDataBuilder setReferenceCode(String referenceCode) {
            this.referenceCode = referenceCode;
            return this;
        }

        public DocumentMetaDataDataBuilder setTags(List<String> tags) {
            this.tags = tags;
            return this;
        }

        public DocumentMetaDataData build() {
            return new DocumentMetaDataData(this);
        }

    }

}
