package hu.codeandsoda.osa.account.user.data;

import java.util.ArrayList;
import java.util.List;

public class UserAccountData {

    private Long id;

    private String email;

    private String name;

    private String displayName;

    private List<UserImageData> images;

    private List<UserDocumentData> documents;

    private List<String> publishedDocuments;

    private List<UserCollectionData> collections;

    private List<UserPublishedDocumentAnnotationData> annotations;

    public UserAccountData() {
        images = new ArrayList<>();
        documents = new ArrayList<>();
        publishedDocuments = new ArrayList<>();
        collections = new ArrayList<>();
        annotations = new ArrayList<>();
    }

    private UserAccountData(UserAccountDataBuilder userAccountDataBuilder) {
        id = userAccountDataBuilder.id;
        email = userAccountDataBuilder.email;
        name = userAccountDataBuilder.name;
        displayName = userAccountDataBuilder.displayName;
        images = userAccountDataBuilder.images;
        documents = userAccountDataBuilder.documents;
        publishedDocuments = userAccountDataBuilder.publishedDocuments;
        collections = userAccountDataBuilder.collections;
        annotations = userAccountDataBuilder.annotations;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public List<UserImageData> getImages() {
        return images;
    }

    public void setImages(List<UserImageData> images) {
        this.images = images;
    }

    public List<UserDocumentData> getDocuments() {
        return documents;
    }

    public void setDocuments(List<UserDocumentData> documents) {
        this.documents = documents;
    }

    public List<String> getPublishedDocuments() {
        return publishedDocuments;
    }

    public void setPublishedDocuments(List<String> publishedDocuments) {
        this.publishedDocuments = publishedDocuments;
    }

    public List<UserCollectionData> getCollections() {
        return collections;
    }

    public void setCollections(List<UserCollectionData> collections) {
        this.collections = collections;
    }

    public List<UserPublishedDocumentAnnotationData> getAnnotations() {
        return annotations;
    }

    public void setAnnotations(List<UserPublishedDocumentAnnotationData> annotations) {
        this.annotations = annotations;
    }

    public static class UserAccountDataBuilder {

        private Long id;

        private String email;

        private String name;

        private String displayName;

        private List<UserImageData> images;

        private List<UserDocumentData> documents;

        private List<String> publishedDocuments;

        private List<UserCollectionData> collections;

        private List<UserPublishedDocumentAnnotationData> annotations;

        public UserAccountDataBuilder() {
            images = new ArrayList<>();
            documents = new ArrayList<>();
            publishedDocuments = new ArrayList<>();
            collections = new ArrayList<>();
            annotations = new ArrayList<>();
        }

        public UserAccountDataBuilder setId(Long id) {
            this.id = id;
            return this;
        }

        public UserAccountDataBuilder setEmail(String email) {
            this.email = email;
            return this;
        }

        public UserAccountDataBuilder setName(String name) {
            this.name = name;
            return this;
        }

        public UserAccountDataBuilder setDisplayName(String displayName) {
            this.displayName = displayName;
            return this;
        }

        public UserAccountDataBuilder setImages(List<UserImageData> images) {
            this.images = images;
            return this;
        }

        public UserAccountDataBuilder setDocuments(List<UserDocumentData> documents) {
            this.documents = documents;
            return this;
        }

        public UserAccountDataBuilder setPublishedDocuments(List<String> publishedDocuments) {
            this.publishedDocuments = publishedDocuments;
            return this;
        }

        public UserAccountDataBuilder setCollections(List<UserCollectionData> collections) {
            this.collections = collections;
            return this;
        }

        public UserAccountDataBuilder setAnnotations(List<UserPublishedDocumentAnnotationData> annotations) {
            this.annotations = annotations;
            return this;
        }

        public UserAccountData build() {
            return new UserAccountData(this);
        }

    }


}
