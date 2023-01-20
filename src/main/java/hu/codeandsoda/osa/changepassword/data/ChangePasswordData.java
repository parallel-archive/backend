package hu.codeandsoda.osa.changepassword.data;

import java.util.Objects;

import org.springframework.util.ObjectUtils;

public class ChangePasswordData {

    private String oldPassword;

    private String newPassword;

    private String repeatNewPassword;

    public ChangePasswordData() {
    }

    private ChangePasswordData(ChangePasswordDataBuilder changePasswordDataBuilder) {
        oldPassword = changePasswordDataBuilder.oldPassword;
        newPassword = changePasswordDataBuilder.newPassword;
        repeatNewPassword = changePasswordDataBuilder.repeatNewPassword;
    }

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getRepeatNewPassword() {
        return repeatNewPassword;
    }

    public void setRepeatNewPassword(String repeatNewPassword) {
        this.repeatNewPassword = repeatNewPassword;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof ChangePasswordData) {
            ChangePasswordData d = (ChangePasswordData) o;
            return ObjectUtils.nullSafeEquals(o, d);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(oldPassword, newPassword, repeatNewPassword);
    }

    public static class ChangePasswordDataBuilder {

        private String oldPassword;

        private String newPassword;

        private String repeatNewPassword;

        public ChangePasswordDataBuilder() {
        }

        public ChangePasswordDataBuilder setOldPassword(String oldPassword) {
            this.oldPassword = oldPassword;
            return this;
        }

        public ChangePasswordDataBuilder setNewPassword(String newPassword) {
            this.newPassword = newPassword;
            return this;
        }

        public ChangePasswordDataBuilder setRepeatNewPassword(String repeatNewPassword) {
            this.repeatNewPassword = repeatNewPassword;
            return this;
        }

        public ChangePasswordData build() {
            return new ChangePasswordData(this);
        }
    }

}
