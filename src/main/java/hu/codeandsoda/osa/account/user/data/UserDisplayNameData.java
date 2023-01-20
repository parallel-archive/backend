package hu.codeandsoda.osa.account.user.data;

public class UserDisplayNameData {

    private String email;

    private String displayName;

    private boolean publicEmail;

    public UserDisplayNameData() {
    }

    private UserDisplayNameData(UserDisplayNameDataBuilder userDisplayNameDataBuilder) {
        email = userDisplayNameDataBuilder.email;
        displayName = userDisplayNameDataBuilder.displayName;
        publicEmail = userDisplayNameDataBuilder.publicEmail;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public boolean isPublicEmail() {
        return publicEmail;
    }

    public void setPublicEmail(boolean publicEmail) {
        this.publicEmail = publicEmail;
    }

    public static class UserDisplayNameDataBuilder {

        private String email;

        private String displayName;

        private boolean publicEmail;

        public UserDisplayNameDataBuilder() {
        }

        public UserDisplayNameDataBuilder setEmail(String email) {
            this.email = email;
            return this;
        }

        public UserDisplayNameDataBuilder setDisplayName(String displayName) {
            this.displayName = displayName;
            return this;
        }

        public UserDisplayNameDataBuilder setPublicEmail(boolean publicEmail) {
            this.publicEmail = publicEmail;
            return this;
        }

        public UserDisplayNameData build() {
            return new UserDisplayNameData(this);
        }
    }

}
