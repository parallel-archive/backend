package hu.codeandsoda.osa.myshoebox.data;

import java.util.ArrayList;
import java.util.List;

public class DeleteImagesData {

    private List<String> urls;

    public DeleteImagesData() {
    }

    private DeleteImagesData(DeleteImagesDataBuilder deleteImagesDataBuilder) {
        urls = deleteImagesDataBuilder.urls;
    }

    public List<String> getUrls() {
        return urls;
    }

    public void setUrls(List<String> urls) {
        this.urls = urls;
    }

    public static class DeleteImagesDataBuilder {

        private List<String> urls;

        public DeleteImagesDataBuilder() {
            urls = new ArrayList<>();
        }

        public DeleteImagesDataBuilder setUrls(List<String> urls) {
            this.urls = urls;
            return this;
        }

        public DeleteImagesData build() {
            return new DeleteImagesData(this);
        }

    }
}
