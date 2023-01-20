package hu.codeandsoda.osa.pagination.data;


public class PaginationRequest {

    private int size;

    private int page;

    public PaginationRequest() {
    }

    public PaginationRequest(PaginationRequestBuilder paginationRequestBuilder) {
        this.size = paginationRequestBuilder.size;
        this.page = paginationRequestBuilder.page;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public static class PaginationRequestBuilder {

        private int size;

        private int page;

        public PaginationRequestBuilder setSize(int size) {
            this.size = size;
            return this;
        }

        public PaginationRequestBuilder setPage(int page) {
            this.page = page;
            return this;
        }

        public PaginationRequest build() {
            return new PaginationRequest(this);
        }
    }

}
