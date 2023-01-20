package hu.codeandsoda.osa.registration.data;

import org.springframework.util.ObjectUtils;

public class RegistrationTokenData {

    private String token;

    public RegistrationTokenData() {
    }

    private RegistrationTokenData(RegistrationTokenDataBuilder registrationTokenDataBuilder) {
        token = registrationTokenDataBuilder.token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof RegistrationTokenData) {
            RegistrationTokenData d = (RegistrationTokenData) o;
            return ObjectUtils.nullSafeEquals(o, d);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return token.hashCode();
    }

    public static class RegistrationTokenDataBuilder {

        private String token;

        public RegistrationTokenDataBuilder() {
        }

        public RegistrationTokenDataBuilder setToken(String token) {
            this.token = token;
            return this;
        }

        public RegistrationTokenData build() {
            return new RegistrationTokenData(this);
        }
    }

}
