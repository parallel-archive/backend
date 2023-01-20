package hu.codeandsoda.osa.documentpublish.data;

import java.util.ArrayList;
import java.util.List;

public class PublishedDocumentMetaDataData {

    private String originalTitle;

    private String originalAuthor;

    private Integer createdAtYear;

    private List<String> types;

    private List<String> languages;

    private Integer periodCoveredFrom;

    private Integer periodCoveredTo;

    private List<String> countriesCovered;

    private String archiveName;

    private String archiveCategory;

    private String publication;

    private String catalogUrl;

    private String sourceUrl;

    private String referenceCode;

    private List<String> tags;

    public PublishedDocumentMetaDataData() {
        types = new ArrayList<>();
        languages = new ArrayList<>();
        countriesCovered = new ArrayList<>();
        tags = new ArrayList<>();
    }

    private PublishedDocumentMetaDataData(PublishedDocumentMetaDataDataBuilder publishedDocumentMetaDataDataBuilder) {
        originalTitle = publishedDocumentMetaDataDataBuilder.originalTitle;
        originalAuthor = publishedDocumentMetaDataDataBuilder.originalAuthor;
        createdAtYear = publishedDocumentMetaDataDataBuilder.createdAtYear;
        types = publishedDocumentMetaDataDataBuilder.types;
        languages = publishedDocumentMetaDataDataBuilder.languages;
        periodCoveredFrom = publishedDocumentMetaDataDataBuilder.periodCoveredFrom;
        periodCoveredTo = publishedDocumentMetaDataDataBuilder.periodCoveredTo;
        countriesCovered = publishedDocumentMetaDataDataBuilder.countriesCovered;
        archiveName = publishedDocumentMetaDataDataBuilder.archiveName;
        archiveCategory = publishedDocumentMetaDataDataBuilder.archiveCategory;
        publication = publishedDocumentMetaDataDataBuilder.publication;
        catalogUrl = publishedDocumentMetaDataDataBuilder.catalogUrl;
        sourceUrl = publishedDocumentMetaDataDataBuilder.sourceUrl;
        referenceCode = publishedDocumentMetaDataDataBuilder.referenceCode;
        tags = publishedDocumentMetaDataDataBuilder.tags;
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

    public List<String> getTypes() {
        return types;
    }

    public void setTypes(List<String> types) {
        this.types = types;
    }

    public List<String> getLanguages() {
        return languages;
    }

    public void setLanguages(List<String> languages) {
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

    public List<String> getCountriesCovered() {
        return countriesCovered;
    }

    public void setCountriesCovered(List<String> countriesCovered) {
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

    public static class PublishedDocumentMetaDataDataBuilder {

        private String originalTitle;

        private String originalAuthor;

        private Integer createdAtYear;

        private List<String> types;

        private List<String> languages;

        private Integer periodCoveredFrom;

        private Integer periodCoveredTo;

        private List<String> countriesCovered;

        private String archiveName;

        private String archiveCategory;

        private String publication;

        private String catalogUrl;

        private String sourceUrl;

        private String referenceCode;

        private List<String> tags;

        public PublishedDocumentMetaDataDataBuilder() {
            types = new ArrayList<>();
            languages = new ArrayList<>();
            countriesCovered = new ArrayList<>();
            tags = new ArrayList<>();
        }

        public PublishedDocumentMetaDataDataBuilder setOriginalTitle(String originalTitle) {
            this.originalTitle = originalTitle;
            return this;
        }

        public PublishedDocumentMetaDataDataBuilder setOriginalAuthor(String originalAuthor) {
            this.originalAuthor = originalAuthor;
            return this;
        }

        public PublishedDocumentMetaDataDataBuilder setCreatedAtYear(Integer createdAtYear) {
            this.createdAtYear = createdAtYear;
            return this;
        }

        public PublishedDocumentMetaDataDataBuilder setTypes(List<String> types) {
            this.types = types;
            return this;
        }

        public PublishedDocumentMetaDataDataBuilder setLanguages(List<String> languages) {
            this.languages = languages;
            return this;
        }

        public PublishedDocumentMetaDataDataBuilder setPeriodCoveredFrom(Integer periodCoveredFrom) {
            this.periodCoveredFrom = periodCoveredFrom;
            return this;
        }

        public PublishedDocumentMetaDataDataBuilder setPeriodCoveredTo(Integer periodCoveredTo) {
            this.periodCoveredTo = periodCoveredTo;
            return this;
        }

        public PublishedDocumentMetaDataDataBuilder setCountriesCovered(List<String> countriesCovered) {
            this.countriesCovered = countriesCovered;
            return this;
        }

        public PublishedDocumentMetaDataDataBuilder setArchiveName(String archiveName) {
            this.archiveName = archiveName;
            return this;
        }

        public PublishedDocumentMetaDataDataBuilder setArchiveCategory(String archiveCategory) {
            this.archiveCategory = archiveCategory;
            return this;
        }

        public PublishedDocumentMetaDataDataBuilder setPublication(String publication) {
            this.publication = publication;
            return this;
        }

        public PublishedDocumentMetaDataDataBuilder setCatalogUrl(String catalogUrl) {
            this.catalogUrl = catalogUrl;
            return this;
        }

        public PublishedDocumentMetaDataDataBuilder setSourceUrl(String sourceUrl) {
            this.sourceUrl = sourceUrl;
            return this;
        }

        public PublishedDocumentMetaDataDataBuilder setReferenceCode(String referenceCode) {
            this.referenceCode = referenceCode;
            return this;
        }

        public PublishedDocumentMetaDataDataBuilder setTags(List<String> tags) {
            this.tags = tags;
            return this;
        }

        public PublishedDocumentMetaDataData build() {
            return new PublishedDocumentMetaDataData(this);
        }

    }

}
