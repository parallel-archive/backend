package hu.codeandsoda.osa.document.data;

import java.util.ArrayList;
import java.util.List;

public class DocumentImagesRequestData {

    private Long id;

    private List<Long> imageIds;

    public DocumentImagesRequestData() {
        imageIds = new ArrayList<>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<Long> getImageIds() {
        return imageIds;
    }

    public void setImageIds(List<Long> imageIds) {
        this.imageIds = imageIds;
    }

}
