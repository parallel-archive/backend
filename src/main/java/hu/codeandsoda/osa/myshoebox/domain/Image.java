package hu.codeandsoda.osa.myshoebox.domain;

import java.io.Serializable;
import java.time.ZonedDateTime;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Entity
public class Image implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Image name cannot be blank.")
    private String name;

    @NotBlank(message = "Image url name cannot be blank.")
    private String url;

    private String activeUrl;

    private String thumbnailUrl;
    
    private String activeThumbnailUrl;

    private Integer rotation;

    @NotNull
    private ZonedDateTime uploadedAt;

    @NotNull
    private ZonedDateTime modifiedAt;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "my_shoe_box_id")
    @NotNull
    private MyShoeBox myShoeBox;

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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getActiveUrl() {
        return activeUrl;
    }

    public void setActiveUrl(String activeUrl) {
        this.activeUrl = activeUrl;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public String getActiveThumbnailUrl() {
        return activeThumbnailUrl;
    }

    public void setActiveThumbnailUrl(String activeThumbnailUrl) {
        this.activeThumbnailUrl = activeThumbnailUrl;
    }

    public Integer getRotation() {
        return rotation;
    }

    public void setRotation(Integer rotation) {
        this.rotation = rotation;
    }

    public ZonedDateTime getUploadedAt() {
        return uploadedAt;
    }

    public void setUploadedAt(ZonedDateTime uploadedAt) {
        this.uploadedAt = uploadedAt;
    }

    public ZonedDateTime getModifiedAt() {
        return modifiedAt;
    }

    public void setModifiedAt(ZonedDateTime modifiedAt) {
        this.modifiedAt = modifiedAt;
    }

    public MyShoeBox getMyShoeBox() {
        return myShoeBox;
    }

    public void setMyShoeBox(MyShoeBox myShoeBox) {
        this.myShoeBox = myShoeBox;
    }

}
