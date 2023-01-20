package hu.codeandsoda.osa.ocr.domain;

import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

import hu.codeandsoda.osa.account.user.domain.User;

@Entity
public class OcrRateLimit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int page;

    @OneToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "FK_OCR_RATE_LIMIT_USER_ID"), unique = true)
    private User user;

    public OcrRateLimit() {
    }

    public OcrRateLimit(User user, int page) {
        this.user = user;
        this.page = page;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

}
