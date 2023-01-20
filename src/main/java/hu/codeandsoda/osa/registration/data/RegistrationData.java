package hu.codeandsoda.osa.registration.data;

import org.springframework.util.ObjectUtils;

public class RegistrationData {

    private String email;

    private String password;

    private String repeatPassword;

    public RegistrationData() {
    }

    private RegistrationData(RegistrationDataBuilder registrationDataBuilder) {
        email = registrationDataBuilder.email;
        password = registrationDataBuilder.password;
        repeatPassword = registrationDataBuilder.repeatPassword;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRepeatPassword() {
        return repeatPassword;
    }

    public void setRepeatPassword(String repeatPassword) {
        this.repeatPassword = repeatPassword;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof RegistrationData) {
            RegistrationData d = (RegistrationData) o;
            return ObjectUtils.nullSafeEquals(o, d);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return email.hashCode() ^ password.hashCode() ^ repeatPassword.hashCode();
    }

    public static class RegistrationDataBuilder {

        private String email;

        private String password;

        private String repeatPassword;

        public RegistrationDataBuilder() {
        }

        public RegistrationDataBuilder setEmail(String email) {
            this.email = email;
            return this;
        }

        public RegistrationDataBuilder setPassword(String password) {
            this.password = password;
            return this;
        }

        public RegistrationDataBuilder setRepeatPassword(String repeatPassword) {
            this.repeatPassword = repeatPassword;
            return this;
        }

        public RegistrationData build() {
            return new RegistrationData(this);
        }

    }

}
