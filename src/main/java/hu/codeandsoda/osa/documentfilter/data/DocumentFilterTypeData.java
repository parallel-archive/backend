package hu.codeandsoda.osa.documentfilter.data;

import java.util.ArrayList;
import java.util.List;

public class DocumentFilterTypeData {

    private Long id;

    private DocumentFilterTypeName name;

    private List<DocumentFilterData> filters;

    public DocumentFilterTypeData() {
        filters = new ArrayList<>();
    }

    private DocumentFilterTypeData(DocumentFilterTypeDataBuilder documentFilterTypeDataBuilder) {
        id = documentFilterTypeDataBuilder.id;
        name = documentFilterTypeDataBuilder.name;
        filters = documentFilterTypeDataBuilder.filters;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public DocumentFilterTypeName getName() {
        return name;
    }

    public void setName(DocumentFilterTypeName name) {
        this.name = name;
    }

    public List<DocumentFilterData> getFilters() {
        return filters;
    }

    public void setFilters(List<DocumentFilterData> filters) {
        this.filters = filters;
    }

    public static class DocumentFilterTypeDataBuilder {

        private Long id;

        private DocumentFilterTypeName name;

        private List<DocumentFilterData> filters;

        public DocumentFilterTypeDataBuilder() {
            filters = new ArrayList<>();
        }

        public DocumentFilterTypeDataBuilder setId(Long id) {
            this.id = id;
            return this;
        }

        public DocumentFilterTypeDataBuilder setName(DocumentFilterTypeName name) {
            this.name = name;
            return this;
        }

        public DocumentFilterTypeDataBuilder setFilters(List<DocumentFilterData> filters) {
            this.filters = filters;
            return this;
        }

        public DocumentFilterTypeData build() {
            return new DocumentFilterTypeData(this);
        }

    }

}
