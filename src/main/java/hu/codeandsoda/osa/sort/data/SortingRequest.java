package hu.codeandsoda.osa.sort.data;

public class SortingRequest {

    private String sort;

    private String sortBy;

    public SortingRequest() {
    }

    private SortingRequest(SortingRequestBuilder sortingRequestBuilder) {
        this.sort = sortingRequestBuilder.sort;
        this.sortBy = sortingRequestBuilder.sortBy;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public String getSortBy() {
        return sortBy;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }

    public static class SortingRequestBuilder {

        private String sort;

        private String sortBy;

        public SortingRequestBuilder setSort(String sort) {
            this.sort = sort;
            return this;
        }

        public SortingRequestBuilder setSortBy(String sortBy) {
            this.sortBy = sortBy;
            return this;
        }

        public SortingRequest build() {
            return new SortingRequest(this);
        }

    }

}
