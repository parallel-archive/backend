package hu.codeandsoda.osa.account.user.data;

import java.util.ArrayList;
import java.util.List;

public class UserCollectionData {

    private String name;

    private List<String> publishedDocuments;

    public UserCollectionData() {
        publishedDocuments = new ArrayList<>();
    }

    private UserCollectionData(UserCollectionDataBuilder userCollectionDataBuilder) {
        name = userCollectionDataBuilder.name;
        publishedDocuments = userCollectionDataBuilder.publishedDocuments;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getPublishedDocuments() {
        return publishedDocuments;
    }

    public void setPublishedDocuments(List<String> publishedDocuments) {
        this.publishedDocuments = publishedDocuments;
    }

    public static class UserCollectionDataBuilder {

        private String name;

        private List<String> publishedDocuments;

        public UserCollectionDataBuilder() {
            publishedDocuments = new ArrayList<>();
        }

        public UserCollectionDataBuilder setName(String name) {
            this.name = name;
            return this;
        }

        public UserCollectionDataBuilder setPublishedDocuments(List<String> publishedDocuments) {
            this.publishedDocuments = publishedDocuments;
            return this;
        }

        public UserCollectionData build() {
            return new UserCollectionData(this);
        }

    }

}
