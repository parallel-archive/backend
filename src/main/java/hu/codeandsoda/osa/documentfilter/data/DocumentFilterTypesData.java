package hu.codeandsoda.osa.documentfilter.data;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DocumentFilterTypesData {

    private List<DocumentFilterTypeData> filterTypes;

    private PeriodFilterData periodFilter;

    private Set<String> tags;

    public DocumentFilterTypesData() {
        filterTypes = new ArrayList<>();
        periodFilter = new PeriodFilterData();
        tags = new HashSet<>();
    }

    private DocumentFilterTypesData(DocumentFilterTypesDataBuilder documentFilterTypesDataBuilder) {
        filterTypes = documentFilterTypesDataBuilder.filterTypes;
        periodFilter = documentFilterTypesDataBuilder.periodFilter;
        tags = documentFilterTypesDataBuilder.tags;
    }

    public List<DocumentFilterTypeData> getFilterTypes() {
        return filterTypes;
    }

    public void setFilterTypes(List<DocumentFilterTypeData> filterTypes) {
        this.filterTypes = filterTypes;
    }

    public PeriodFilterData getPeriodFilter() {
        return periodFilter;
    }

    public void setPeriodFilter(PeriodFilterData periodFilter) {
        this.periodFilter = periodFilter;
    }

    public Set<String> getTags() {
        return tags;
    }

    public void setTags(Set<String> tags) {
        this.tags = tags;
    }

    public static class DocumentFilterTypesDataBuilder {

        private List<DocumentFilterTypeData> filterTypes;

        private PeriodFilterData periodFilter;

        private Set<String> tags;

        public DocumentFilterTypesDataBuilder() {
            filterTypes = new ArrayList<>();
            periodFilter = new PeriodFilterData();
            tags = new HashSet<>();
        }

        public DocumentFilterTypesDataBuilder setFilterTypes(List<DocumentFilterTypeData> filterTypes) {
            this.filterTypes = filterTypes;
            return this;
        }

        public DocumentFilterTypesDataBuilder setPeriodFilter(PeriodFilterData periodFilter) {
            this.periodFilter = periodFilter;
            return this;
        }

        public DocumentFilterTypesDataBuilder setTags(Set<String> tags) {
            this.tags = tags;
            return this;
        }

        public DocumentFilterTypesData build() {
            return new DocumentFilterTypesData(this);
        }

    }

}
