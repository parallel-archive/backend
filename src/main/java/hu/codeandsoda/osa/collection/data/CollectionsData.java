package hu.codeandsoda.osa.collection.data;

import java.util.ArrayList;
import java.util.List;

public class CollectionsData {

    private List<CollectionData> collections;

    public CollectionsData() {
        collections = new ArrayList<>();
    }

    private CollectionsData(CollectionsDataBuilder collectionsDataBuilder) {
        collections = collectionsDataBuilder.collections;
    }

    public List<CollectionData> getCollections() {
        return collections;
    }

    public void setCollections(List<CollectionData> collections) {
        this.collections = collections;
    }

    public static class CollectionsDataBuilder {

        private List<CollectionData> collections;

        public CollectionsDataBuilder() {
            collections = new ArrayList<>();
        }

        public CollectionsDataBuilder setCollections(List<CollectionData> collections) {
            this.collections = collections;
            return this;
        }

        public CollectionsData build() {
            return new CollectionsData(this);
        }

    }


}
