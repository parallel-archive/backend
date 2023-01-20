package hu.codeandsoda.osa.document.data;

import java.util.ArrayList;
import java.util.List;

public class DocumentMetaDataRequestData {

    private String originalTitle;

    private String originalAuthor;

    private Integer createdAtYear;

    private List<Long> types;

    private List<Long> languages;

    private Integer periodCoveredFrom;

    private Integer periodCoveredTo;

    private List<Long> countriesCovered;

    private String archiveName;

    private String archiveCategory;

    private String publication;

    private String catalogUrl;

    private String sourceUrl;

    private String referenceCode;

    private List<String> tags;

    public DocumentMetaDataRequestData() {
        types = new ArrayList<>();
        languages = new ArrayList<>();
        countriesCovered = new ArrayList<>();
        tags = new ArrayList<>();
    }

    private DocumentMetaDataRequestData(DocumentMetaDataRequestDataBuilder documentMetaDataRequestDataBuilder) {
        originalTitle = documentMetaDataRequestDataBuilder.originalTitle;
        originalAuthor = documentMetaDataRequestDataBuilder.originalAuthor;
        createdAtYear = documentMetaDataRequestDataBuilder.createdAtYear;
        types = documentMetaDataRequestDataBuilder.types;
        languages = documentMetaDataRequestDataBuilder.languages;
        periodCoveredFrom = documentMetaDataRequestDataBuilder.periodCoveredFrom;
        periodCoveredTo = documentMetaDataRequestDataBuilder.periodCoveredTo;
        countriesCovered = documentMetaDataRequestDataBuilder.countriesCovered;
        archiveName = documentMetaDataRequestDataBuilder.archiveName;
        archiveCategory = documentMetaDataRequestDataBuilder.archiveCategory;
        publication = documentMetaDataRequestDataBuilder.publication;
        catalogUrl = documentMetaDataRequestDataBuilder.catalogUrl;
        sourceUrl = documentMetaDataRequestDataBuilder.sourceUrl;
        referenceCode = documentMetaDataRequestDataBuilder.referenceCode;
        tags = documentMetaDataRequestDataBuilder.tags;
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

    public List<Long> getTypes() {
        return types;
    }

    public void setTypes(List<Long> types) {
        this.types = types;
    }

    public List<Long> getLanguages() {
        return languages;
    }

    public void setLanguages(List<Long> languages) {
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

    public List<Long> getCountriesCovered() {
        return countriesCovered;
    }

    public void setCountriesCovered(List<Long> countriesCovered) {
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

    public static class DocumentMetaDataRequestDataBuilder {

        private String originalTitle;

        private String originalAuthor;

        private Integer createdAtYear;

        private List<Long> types;

        private List<Long> languages;

        private Integer periodCoveredFrom;

        private Integer periodCoveredTo;

        private List<Long> countriesCovered;

        private String archiveName;

        private String archiveCategory;

        private String publication;

        private String catalogUrl;

        private String sourceUrl;
        
        private String referenceCode;

        private List<String> tags;

        public DocumentMetaDataRequestDataBuilder() {
            types = new ArrayList<>();
            languages = new ArrayList<>();
            countriesCovered = new ArrayList<>();
            tags = new ArrayList<>();
        }

        public DocumentMetaDataRequestDataBuilder setOriginalTitle(String originalTitle) {
            this.originalTitle = originalTitle;
            return this;
        }
        
        public DocumentMetaDataRequestDataBuilder setOriginalAuthor(String originalAuthor) {
            this.originalAuthor = originalAuthor;
            return this;
        }

        public DocumentMetaDataRequestDataBuilder setCreatedAtYear(Integer createdAtYear) {
            this.createdAtYear = createdAtYear;
            return this;
        }
        
        public DocumentMetaDataRequestDataBuilder setTypes(List<Long> types) {
            this.types = types;
            return this;
        }
        
        public DocumentMetaDataRequestDataBuilder setLanguages(List<Long> languages) {
            this.languages = languages;
            return this;
        }
        
        public DocumentMetaDataRequestDataBuilder setPeriodCoveredFrom(Integer periodCoveredFrom) {
            this.periodCoveredFrom = periodCoveredFrom;
            return this;
        }
        
        public DocumentMetaDataRequestDataBuilder setPeriodCoveredTo(Integer periodCoveredTo) {
            this.periodCoveredTo = periodCoveredTo;
            return this;
        }

        public DocumentMetaDataRequestDataBuilder setCountriesCovered(List<Long> countriesCovered) {
            this.countriesCovered = countriesCovered;
            return this;
        }
        
        public DocumentMetaDataRequestDataBuilder setArchiveName(String archiveName) {
            this.archiveName = archiveName;
            return this;
        }

        public DocumentMetaDataRequestDataBuilder setArchiveCategory(String archiveCategory) {
            this.archiveCategory = archiveCategory;
            return this;
        }

        public DocumentMetaDataRequestDataBuilder setPublication(String publication) {
            this.publication = publication;
            return this;
        }
        
        public DocumentMetaDataRequestDataBuilder setCatalogUrl(String catalogUrl) {
            this.catalogUrl = catalogUrl;
            return this;
        }
        
        public DocumentMetaDataRequestDataBuilder setSourceUrl(String sourceUrl) {
            this.sourceUrl = sourceUrl;
            return this;
        }
        
        public DocumentMetaDataRequestDataBuilder setReferenceCode(String referenceCode) {
            this.referenceCode = referenceCode;
            return this;
        }

        public DocumentMetaDataRequestDataBuilder setTags(List<String> tags) {
            this.tags = tags;
            return this;
        }

        public DocumentMetaDataRequestData build() {
            return new DocumentMetaDataRequestData(this);
        }
        
    }

}
