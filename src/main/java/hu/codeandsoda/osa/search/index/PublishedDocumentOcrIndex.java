package hu.codeandsoda.osa.search.index;


public class PublishedDocumentOcrIndex {

    private String editedOcr;

    public PublishedDocumentOcrIndex() {
    }

    public PublishedDocumentOcrIndex(String editedOcr) {
        this.editedOcr = editedOcr;
    }

    public String getEditedOcr() {
        return editedOcr;
    }

    public void setEditedOcr(String editedOcr) {
        this.editedOcr = editedOcr;
    }

}
