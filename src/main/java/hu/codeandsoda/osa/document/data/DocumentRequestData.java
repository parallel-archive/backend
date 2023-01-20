package hu.codeandsoda.osa.document.data;

import java.util.ArrayList;
import java.util.List;

public class DocumentRequestData {

    private Long id;

    private DocumentMetaDataRequestData metaDataRequest;

    private List<DocumentImageRequestData> images;

    public DocumentRequestData() {
        images = new ArrayList<>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public DocumentMetaDataRequestData getMetaDataRequest() {
        return metaDataRequest;
    }

    public void setMetaDataRequest(DocumentMetaDataRequestData metaDataRequest) {
        this.metaDataRequest = metaDataRequest;
    }

    public List<DocumentImageRequestData> getImages() {
        return images;
    }

    public void setImages(List<DocumentImageRequestData> images) {
        this.images = images;
    }

}
