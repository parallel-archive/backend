package hu.codeandsoda.osa.account.user.data;

import org.springframework.util.ObjectUtils;

public class UserData {

    private String name;

    public UserData() {
    }

    private UserData(UserDataBuilder userDataBuilder) {
        name = userDataBuilder.name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof UserData) {
            UserData d = (UserData) o;
            return ObjectUtils.nullSafeEquals(o, d);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    public static class UserDataBuilder {

        private String name;

        public UserDataBuilder() {
        }

        public UserDataBuilder setName(String name) {
            this.name = name;
            return this;
        }

        public UserData build() {
            return new UserData(this);
        }
    }

}
