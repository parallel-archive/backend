package hu.codeandsoda.osa.account.user.data;

import java.util.ArrayList;
import java.util.List;

public class UserDocumentMetaDataData {

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

    public UserDocumentMetaDataData() {
        types = new ArrayList<>();
        languages = new ArrayList<>();
        countriesCovered = new ArrayList<>();
        tags = new ArrayList<>();
    }

    public UserDocumentMetaDataData(UserDocumentMetaDataDataBuilder userDocumentMetaDataDataBuilder) {
        originalTitle = userDocumentMetaDataDataBuilder.originalTitle;
        originalAuthor = userDocumentMetaDataDataBuilder.originalAuthor;
        createdAtYear = userDocumentMetaDataDataBuilder.createdAtYear;
        types = userDocumentMetaDataDataBuilder.types;
        languages = userDocumentMetaDataDataBuilder.languages;
        periodCoveredFrom = userDocumentMetaDataDataBuilder.periodCoveredFrom;
        periodCoveredTo = userDocumentMetaDataDataBuilder.periodCoveredTo;
        countriesCovered = userDocumentMetaDataDataBuilder.countriesCovered;
        archiveName = userDocumentMetaDataDataBuilder.archiveName;
        archiveCategory = userDocumentMetaDataDataBuilder.archiveCategory;
        publication = userDocumentMetaDataDataBuilder.publication;
        catalogUrl = userDocumentMetaDataDataBuilder.catalogUrl;
        sourceUrl = userDocumentMetaDataDataBuilder.sourceUrl;
        referenceCode = userDocumentMetaDataDataBuilder.referenceCode;
        tags = userDocumentMetaDataDataBuilder.tags;
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

    public static class UserDocumentMetaDataDataBuilder {

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

        public UserDocumentMetaDataDataBuilder() {
            types = new ArrayList<>();
            languages = new ArrayList<>();
            countriesCovered = new ArrayList<>();
            tags = new ArrayList<>();
        }

        public UserDocumentMetaDataDataBuilder setOriginalTitle(String originalTitle) {
            this.originalTitle = originalTitle;
            return this;
        }

        public UserDocumentMetaDataDataBuilder setOriginalAuthor(String originalAuthor) {
            this.originalAuthor = originalAuthor;
            return this;
        }

        public UserDocumentMetaDataDataBuilder setCreatedAtYear(Integer createdAtYear) {
            this.createdAtYear = createdAtYear;
            return this;
        }

        public UserDocumentMetaDataDataBuilder setTypes(List<String> types) {
            this.types = types;
            return this;
        }

        public UserDocumentMetaDataDataBuilder setLanguages(List<String> languages) {
            this.languages = languages;
            return this;
        }

        public UserDocumentMetaDataDataBuilder setPeriodCoveredFrom(Integer periodCoveredFrom) {
            this.periodCoveredFrom = periodCoveredFrom;
            return this;
        }

        public UserDocumentMetaDataDataBuilder setPeriodCoveredTo(Integer periodCoveredTo) {
            this.periodCoveredTo = periodCoveredTo;
            return this;
        }

        public UserDocumentMetaDataDataBuilder setCountriesCovered(List<String> countriesCovered) {
            this.countriesCovered = countriesCovered;
            return this;
        }

        public UserDocumentMetaDataDataBuilder setArchiveName(String archiveName) {
            this.archiveName = archiveName;
            return this;
        }

        public UserDocumentMetaDataDataBuilder setArchiveCategory(String archiveCategory) {
            this.archiveCategory = archiveCategory;
            return this;
        }

        public UserDocumentMetaDataDataBuilder setPublication(String publication) {
            this.publication = publication;
            return this;
        }

        public UserDocumentMetaDataDataBuilder setCatalogUrl(String catalogUrl) {
            this.catalogUrl = catalogUrl;
            return this;
        }

        public UserDocumentMetaDataDataBuilder setSourceUrl(String sourceUrl) {
            this.sourceUrl = sourceUrl;
            return this;
        }

        public UserDocumentMetaDataDataBuilder setReferenceCode(String referenceCode) {
            this.referenceCode = referenceCode;
            return this;
        }

        public UserDocumentMetaDataDataBuilder setTags(List<String> tags) {
            this.tags = tags;
            return this;
        }

        public UserDocumentMetaDataData build() {
            return new UserDocumentMetaDataData(this);
        }

    }

}
