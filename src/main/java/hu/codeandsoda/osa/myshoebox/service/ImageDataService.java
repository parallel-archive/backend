package hu.codeandsoda.osa.myshoebox.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import hu.codeandsoda.osa.account.user.data.UserImageData;
import hu.codeandsoda.osa.myshoebox.data.ImageData;
import hu.codeandsoda.osa.myshoebox.domain.Image;

@Service
public class ImageDataService {

    public List<ImageData> constructImageDatas(List<Image> images) {
        List<ImageData> imageDatas = new ArrayList<>();
        for (Image image : images) {
            ImageData imageData = constructImageData(image);
            imageDatas.add(imageData);
        }
        return imageDatas;
    }

    public ImageData constructImageData(Image image) {
        ImageData imageData = new ImageData.ImageDataBuilder().setId(image.getId()).setName(image.getName()).setUrl(image.getUrl()).setActiveUrl(image.getActiveUrl())
                .setThumbnailUrl(image.getThumbnailUrl()).setActiveThumbnailUrl(image.getActiveThumbnailUrl()).setRotation(image.getRotation()).setUploadedAt(image.getUploadedAt())
                .setModifiedAt(image.getModifiedAt()).build();
        return imageData;
    }

    public List<UserImageData> constructUserImageDatas(List<Image> images) {
        List<UserImageData> imageDatas = new ArrayList<>();
        for (Image image : images) {
            UserImageData imageData = constructUserImageData(image);
            imageDatas.add(imageData);
        }
        return imageDatas;
    }

    public UserImageData constructUserImageData(Image image) {
        UserImageData imageData = new UserImageData.UserImageDataBuilder().setName(image.getName()).setUrl(image.getUrl()).setThumbnailUrl(image.getThumbnailUrl())
                .setUploadedAt(image.getUploadedAt()).setModifiedAt(image.getModifiedAt()).build();
        return imageData;
    }

}
