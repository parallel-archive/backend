package hu.codeandsoda.osa.documentpublish.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import hu.codeandsoda.osa.document.domain.DocumentTag;
import hu.codeandsoda.osa.documentfilter.data.DocumentFilterTypeName;
import hu.codeandsoda.osa.documentfilter.data.PeriodFilterData;
import hu.codeandsoda.osa.documentfilter.domain.DocumentFilter;

public class PublicArchivePageFilteringRequest {

    private Map<DocumentFilterTypeName, List<DocumentFilter>> activeFilters;

    private PeriodFilterData periodFilter;

    private List<DocumentTag> activeTags;

    public PublicArchivePageFilteringRequest() {
        this.activeFilters = new HashMap<>();
        this.periodFilter = new PeriodFilterData();
        this.activeTags = new ArrayList<>();
    }

    private PublicArchivePageFilteringRequest(PublicArchivePageFilteringRequestBuilder publicArchivePageFilteringRequestBuilder) {
        this.activeFilters = publicArchivePageFilteringRequestBuilder.activeFilters;
        this.periodFilter = publicArchivePageFilteringRequestBuilder.periodFilter;
        this.activeTags = publicArchivePageFilteringRequestBuilder.activeTags;
    }

    public Map<DocumentFilterTypeName, List<DocumentFilter>> getActiveFilters() {
        return activeFilters;
    }

    public void setActiveFilters(Map<DocumentFilterTypeName, List<DocumentFilter>> activeFilters) {
        this.activeFilters = activeFilters;
    }

    public PeriodFilterData getPeriodFilter() {
        return periodFilter;
    }

    public void setPeriodFilter(PeriodFilterData periodFilter) {
        this.periodFilter = periodFilter;
    }

    public List<DocumentTag> getActiveTags() {
        return activeTags;
    }

    public void setActiveTags(List<DocumentTag> activeTags) {
        this.activeTags = activeTags;
    }

    public static class PublicArchivePageFilteringRequestBuilder {
        
        private Map<DocumentFilterTypeName, List<DocumentFilter>> activeFilters;

        private PeriodFilterData periodFilter;

        private List<DocumentTag> activeTags;

        public PublicArchivePageFilteringRequestBuilder() {
            this.activeFilters = new HashMap<>();
            this.periodFilter = new PeriodFilterData();
            this.activeTags = new ArrayList<>();
        }

        public PublicArchivePageFilteringRequestBuilder setActiveFilters(Map<DocumentFilterTypeName, List<DocumentFilter>> activeFilters) {
            this.activeFilters = activeFilters;
            return this;
        }

        public PublicArchivePageFilteringRequestBuilder setPeriodFilter(PeriodFilterData periodFilter) {
            this.periodFilter = periodFilter;
            return this;
        }

        public PublicArchivePageFilteringRequestBuilder setActiveTags(List<DocumentTag> activeTags) {
            this.activeTags = activeTags;
            return this;
        }

        public PublicArchivePageFilteringRequest build() {
            return new PublicArchivePageFilteringRequest(this);
        }
    }

}
