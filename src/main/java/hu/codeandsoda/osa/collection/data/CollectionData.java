package hu.codeandsoda.osa.collection.data;

public class CollectionData {

    private Long id;

    private String name;

    int collectionSize;

    public CollectionData() {
    }

    private CollectionData(CollectionDataBuilder collectionDataBuilder) {
        id = collectionDataBuilder.id;
        name = collectionDataBuilder.name;
        collectionSize = collectionDataBuilder.collectionSize;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCollectionSize() {
        return collectionSize;
    }

    public void setCollectionSize(int collectionSize) {
        this.collectionSize = collectionSize;
    }

    public static class CollectionDataBuilder {

        private Long id;

        private String name;

        int collectionSize;

        public CollectionDataBuilder setId(Long id) {
            this.id = id;
            return this;
        }

        public CollectionDataBuilder setName(String name) {
            this.name = name;
            return this;
        }

        public CollectionDataBuilder setCollectionSize(int collectionSize) {
            this.collectionSize = collectionSize;
            return this;
        }

        public CollectionData build() {
            return new CollectionData(this);
        }

    }

}
