package hu.codeandsoda.osa.account.user.data;

public class UserDisplayNameRequestData {

    private boolean publicEmail;

    public UserDisplayNameRequestData() {
    }

    private UserDisplayNameRequestData(UserDisplayNameRequestDataBuilder userDisplayNameRequestDataBuilder) {
        publicEmail = userDisplayNameRequestDataBuilder.publicEmail;
    }

    public boolean isPublicEmail() {
        return publicEmail;
    }

    public void setPublicEmail(boolean publicEmail) {
        this.publicEmail = publicEmail;
    }

    public static class UserDisplayNameRequestDataBuilder {

        private boolean publicEmail;

        public UserDisplayNameRequestDataBuilder() {
        }

        public UserDisplayNameRequestDataBuilder setPublicEmail(boolean publicEmail) {
            this.publicEmail = publicEmail;
            return this;
        }

        public UserDisplayNameRequestData build() {
            return new UserDisplayNameRequestData(this);
        }
    }

}
