package hu.codeandsoda.osa.documentfilter.data;

public class DocumentFilterData {

    private Long id;

    private DocumentFilterName name;

    private boolean active;

    public DocumentFilterData() {
    }

    private DocumentFilterData(DocumentFilterDataBuilder documentFiltersDataBuilder) {
        id = documentFiltersDataBuilder.id;
        name = documentFiltersDataBuilder.name;
        active = documentFiltersDataBuilder.active;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public DocumentFilterName getName() {
        return name;
    }

    public void setName(DocumentFilterName name) {
        this.name = name;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public static class DocumentFilterDataBuilder {

        private Long id;

        private DocumentFilterName name;

        private boolean active;

        public DocumentFilterDataBuilder setId(Long id) {
            this.id = id;
            return this;
        }

        public DocumentFilterDataBuilder setName(DocumentFilterName name) {
            this.name = name;
            return this;
        }

        public DocumentFilterDataBuilder setActive(boolean active) {
            this.active = active;
            return this;
        }

        public DocumentFilterData build() {
            return new DocumentFilterData(this);
        }

    }

}
