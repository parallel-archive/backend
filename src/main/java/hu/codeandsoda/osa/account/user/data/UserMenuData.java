package hu.codeandsoda.osa.account.user.data;

public class UserMenuData {

    private Integer myShoeBoxSize;

    private Integer documentsSize;

    public UserMenuData() {
    }

    private UserMenuData(UserMenuDataBuilder userMenuDataBuilder) {
        this.myShoeBoxSize = userMenuDataBuilder.myShoeBoxSize;
        this.documentsSize = userMenuDataBuilder.documentsSize;
    }

    public Integer getMyShoeBoxSize() {
        return myShoeBoxSize;
    }

    public void setMyShoeBoxSize(Integer myShoeBoxSize) {
        this.myShoeBoxSize = myShoeBoxSize;
    }

    public Integer getDocumentsSize() {
        return documentsSize;
    }

    public void setDocumentsSize(Integer documentsSize) {
        this.documentsSize = documentsSize;
    }

    public static class UserMenuDataBuilder {

        private Integer myShoeBoxSize;
        
        private Integer documentsSize;

        public UserMenuDataBuilder() {
        }
       
        public UserMenuDataBuilder setMyShoeBoxSize(Integer myShoeBoxSize) {
            this.myShoeBoxSize = myShoeBoxSize;
            return this;
        }

        public UserMenuDataBuilder setDocumentsSize(Integer documentsSize) {
            this.documentsSize = documentsSize;
            return this;
        }

        public UserMenuData build() {
            return new UserMenuData(this);
        }
    }

}
