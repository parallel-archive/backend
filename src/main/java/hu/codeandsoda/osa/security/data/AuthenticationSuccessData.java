package hu.codeandsoda.osa.security.data;

import hu.codeandsoda.osa.account.user.data.UserData;

public class AuthenticationSuccessData {

    private UserData user;
    private String defaultPath;

    public AuthenticationSuccessData() {
    }

    private AuthenticationSuccessData(AuthenticationSuccesDataBuilder authenticationSuccesDataBuilder) {
        user = authenticationSuccesDataBuilder.user;
        defaultPath = authenticationSuccesDataBuilder.defaultPath;
    }

    public String getDefaultPath() {
        return defaultPath;
    }

    public void setDefaultPath(String defaultPath) {
        this.defaultPath = defaultPath;
    }

    public UserData getUser() {
        return user;
    }

    public void setUser(UserData user) {
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof AuthenticationSuccessData) {
            AuthenticationSuccessData d = (AuthenticationSuccessData) o;
            return user.equals(d.user) && defaultPath.equals(d.defaultPath);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return user.hashCode() ^ defaultPath.hashCode();
    }

    public static class AuthenticationSuccesDataBuilder {

        private UserData user;
        private String defaultPath;

        public AuthenticationSuccesDataBuilder() {
        }

        public AuthenticationSuccesDataBuilder setUser(UserData user) {
            this.user = user;
            return this;
        }

        public AuthenticationSuccesDataBuilder setDefaultPath(String defaultPath) {
            this.defaultPath = defaultPath;
            return this;
        }

        public AuthenticationSuccessData build() {
            return new AuthenticationSuccessData(this);
        }
    }
}
