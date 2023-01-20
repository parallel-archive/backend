package hu.codeandsoda.osa.search.index;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.Setting;

@Document(indexName = "document-tag")
@Setting(settingPath = "/elasticsearch/tag.json")
public class DocumentTagIndex {

    @Id
    private String id;

    @Field(type = FieldType.Text, analyzer = "documentTagAnalyzer")
    private String name;

    public DocumentTagIndex() {
    }

    public DocumentTagIndex(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
