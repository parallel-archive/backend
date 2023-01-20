package hu.codeandsoda.osa.account.user.domain;

import java.util.Collection;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.ObjectUtils;

import hu.codeandsoda.osa.myshoebox.domain.MyShoeBox;

@Entity
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "E-mail cannot be blank.")
    private String email;

    @NotBlank(message = "Password cannot be blank.")
    private String password;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    @JoinColumn(foreignKey = @ForeignKey(name = "FK_USER_DETAIL_ID"))
    private UserDetail userDetail;

    private boolean enabled;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    @JoinColumn(foreignKey = @ForeignKey(name = "FK_MY_SHOE_BOX_ID"))
    @NotNull
    private MyShoeBox myShoeBox;

    @NotBlank(message = "Display name cannot be blank.")
    private String displayName;

    private boolean publicEmail;

    public User() {
        this.enabled = false;
        this.publicEmail = true;
    }

    public User(String email, String password, UserDetail userDetail, MyShoeBox myShoeBox, String displayName) {
        this.email = email;
        this.password = password;
        this.userDetail = userDetail;
        this.myShoeBox = myShoeBox;
        this.displayName = displayName;
        this.publicEmail = true;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public UserDetail getUserDetail() {
        return userDetail;
    }

    public void setUserDetail(UserDetail userDetail) {
        this.userDetail = userDetail;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public MyShoeBox getMyShoeBox() {
        return myShoeBox;
    }

    public void setMyShoeBox(MyShoeBox myShoeBox) {
        this.myShoeBox = myShoeBox;
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

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getUsername() {
        String userName = getEmail();
        if (null != userDetail && null != userDetail.getName()) {
            userName = userDetail.getName();
        }
        return userName;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof User) {
            User user = (User) obj;
            return ObjectUtils.nullSafeEquals(id, user.id);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return id.hashCode() ^ email.hashCode() ^ password.hashCode() ^ displayName.hashCode();
    }

}
