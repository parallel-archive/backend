package hu.codeandsoda.osa.resetpassword.data;

import java.util.Objects;

import org.springframework.util.ObjectUtils;

public class ResetPasswordData {

    private String token;

    private String password;

    private String repeatPassword;

    public ResetPasswordData() {
    }

    private ResetPasswordData(ResetPasswordDataBuilder resetPasswordDataBuilder) {
        token = resetPasswordDataBuilder.token;
        password = resetPasswordDataBuilder.password;
        repeatPassword = resetPasswordDataBuilder.repeatPassword;
    }


    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
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
        if (o instanceof ResetPasswordData) {
            ResetPasswordData d = (ResetPasswordData) o;
            return ObjectUtils.nullSafeEquals(o, d);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(token, password, repeatPassword);
    }

    public static class ResetPasswordDataBuilder {

        private String token;

        private String password;

        private String repeatPassword;

        public ResetPasswordDataBuilder() {
        }

        public ResetPasswordDataBuilder setToken(String token) {
            this.token = token;
            return this;
        }

        public ResetPasswordDataBuilder setPassword(String password) {
            this.password = password;
            return this;
        }

        public ResetPasswordDataBuilder setRepeatPassword(String repeatPassword) {
            this.repeatPassword = repeatPassword;
            return this;
        }

        public ResetPasswordData build() {
            return new ResetPasswordData(this);
        }
    }

}
