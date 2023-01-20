package hu.codeandsoda.osa.search.index;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Document(indexName = "published-document-metadata-tag")
public class PublishedDocumentMetaDataIndex {

    @Id
    private String id;

    @Field(type = FieldType.Text)
    private String originalTitle;

    @Field(type = FieldType.Text)
    private String archiveName;

    @Field(type = FieldType.Text)
    private String archiveCategory;

    @Field(type = FieldType.Text)
    private String publication;

    @Field(type = FieldType.Text)
    private String referenceCode;

    @Field(type = FieldType.Object)
    private List<PublishedDocumentOcrIndex> editedOcrs;

    @Field(type = FieldType.Object)
    private List<PublishedDocumentTagIndex> tags;

    public PublishedDocumentMetaDataIndex() {
        editedOcrs = new ArrayList<>();
        tags = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public void setOriginalTitle(String originalTitle) {
        this.originalTitle = originalTitle;
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

    public String getReferenceCode() {
        return referenceCode;
    }

    public void setReferenceCode(String referenceCode) {
        this.referenceCode = referenceCode;
    }

    public List<PublishedDocumentOcrIndex> getEditedOcrs() {
        return editedOcrs;
    }

    public void setEditedOcrs(List<PublishedDocumentOcrIndex> editedOcrs) {
        this.editedOcrs = editedOcrs;
    }

    public List<PublishedDocumentTagIndex> getTags() {
        return tags;
    }

    public void setTags(List<PublishedDocumentTagIndex> tags) {
        this.tags = tags;
    }
}
