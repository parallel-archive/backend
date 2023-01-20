package hu.codeandsoda.osa.search.data;

import java.util.ArrayList;
import java.util.List;

public class DocumentTagSearchResult {

    private List<String> tags;

    public DocumentTagSearchResult() {
        tags = new ArrayList<>();
    }

    private DocumentTagSearchResult(DocumentTagsDataBuilder documentTagsDataBuilder) {
        tags = documentTagsDataBuilder.tags;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public static class DocumentTagsDataBuilder {

        private List<String> tags;

        public DocumentTagsDataBuilder() {
            tags = new ArrayList<>();
        }

        public DocumentTagsDataBuilder setTags(List<String> tags) {
            this.tags = tags;
            return this;
        }

        public DocumentTagSearchResult build() {
            return new DocumentTagSearchResult(this);
        }

    }

}
