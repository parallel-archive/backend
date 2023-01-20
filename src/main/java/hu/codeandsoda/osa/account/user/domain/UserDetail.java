package hu.codeandsoda.osa.account.user.domain;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;

@Entity
public class UserDetail implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "User name cannot be blank.")
    private String name;

    public UserDetail() {
    }

    public UserDetail(UserDetailBuilder userDetailBuilder) {
        name = userDetailBuilder.name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
        if (o instanceof UserDetail) {
            UserDetail d = (UserDetail) o;
            return id.equals(d.id) && name.equals(d.name);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return id.hashCode() ^ name.hashCode();
    }

    public static class UserDetailBuilder {

        private String name;

        public UserDetailBuilder setName(String name) {
            this.name = name;
            return this;
        }

        public UserDetail build() {
            return new UserDetail(this);
        }

    }
}
