package hu.codeandsoda.osa.myshoebox.data;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.util.ObjectUtils;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import hu.codeandsoda.osa.general.data.ResponseData;
import hu.codeandsoda.osa.general.data.ResponseMessage;

public class MyShoeBoxData extends ResponseData {

    private Long id;

    private String name;

    @JsonIgnoreProperties({ "messages" })
    private Page<ImageData> images;

    public MyShoeBoxData() {
    }

    private MyShoeBoxData(MyShoeBoxDataBuilder myShoeBoxDataBuilder) {
        super(myShoeBoxDataBuilder.messages);
        id = myShoeBoxDataBuilder.id;
        name = myShoeBoxDataBuilder.name;
        images = myShoeBoxDataBuilder.images;
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

    public Page<ImageData> getImages() {
        return images;
    }

    public void setImages(Page<ImageData> images) {
        this.images = images;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof MyShoeBoxData) {
            MyShoeBoxData d = (MyShoeBoxData) o;
            return ObjectUtils.nullSafeEquals(o, d);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return id.hashCode() ^ name.hashCode() ^ images.hashCode();
    }

    public static class MyShoeBoxDataBuilder {

        private Long id;

        private String name;

        private Page<ImageData> images;

        private List<ResponseMessage> messages;

        public MyShoeBoxDataBuilder() {
            images = Page.empty();
            messages = new ArrayList<>();
        }

        public MyShoeBoxDataBuilder setId(Long id) {
            this.id = id;
            return this;
        }

        public MyShoeBoxDataBuilder setName(String name) {
            this.name = name;
            return this;
        }

        public MyShoeBoxDataBuilder setImages(Page<ImageData> images) {
            this.images = images;
            return this;
        }

        public MyShoeBoxDataBuilder setMessages(List<ResponseMessage> messages) {
            this.messages = messages;
            return this;
        }

        public MyShoeBoxData build() {
            return new MyShoeBoxData(this);
        }
    }

}
