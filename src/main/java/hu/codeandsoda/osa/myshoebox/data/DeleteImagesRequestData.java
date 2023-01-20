package hu.codeandsoda.osa.myshoebox.data;

import java.util.List;

public class DeleteImagesRequestData {

    private List<Long> imageIds;

    public List<Long> getImageIds() {
        return imageIds;
    }

    public void setImageIds(List<Long> imageIds) {
        this.imageIds = imageIds;
    }
    
}
