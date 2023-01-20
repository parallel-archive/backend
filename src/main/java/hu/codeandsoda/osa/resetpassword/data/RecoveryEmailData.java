package hu.codeandsoda.osa.resetpassword.data;

import org.springframework.util.ObjectUtils;

public class RecoveryEmailData {

    private String email;

    public RecoveryEmailData() {
    }

    private RecoveryEmailData(RecoveryEmailDataBuilder recoveryEmailDataBuilder) {
        email = recoveryEmailDataBuilder.email;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof RecoveryEmailData) {
            RecoveryEmailData d = (RecoveryEmailData) o;
            return ObjectUtils.nullSafeEquals(o, d);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return email.hashCode();
    }

    public static class RecoveryEmailDataBuilder {

        private String email;

        public RecoveryEmailDataBuilder() {
        }

        public RecoveryEmailDataBuilder setEmail(String email) {
            this.email = email;
            return this;
        }

        public RecoveryEmailData build() {
            return new RecoveryEmailData(this);
        }
    }
}
