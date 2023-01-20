package hu.codeandsoda.osa.myshoebox.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import hu.codeandsoda.osa.account.user.data.UserImageData;
import hu.codeandsoda.osa.myshoebox.data.ImageData;
import hu.codeandsoda.osa.myshoebox.domain.Image;
import hu.codeandsoda.osa.myshoebox.repository.ImageRepository;

@Service
public class ImageService {

    @Autowired
    private ImageDataService imageDataService;

    @Autowired
    private ImageRepository imageRepository;

    public ImageData save(Image image) {
        Image savedImage = imageRepository.save(image);
        ImageData savedImageData = imageDataService.constructImageData(savedImage);
        return savedImageData;
    }

    public boolean userImageExists(Long imageId, Long myShoeBoxId) {
        return imageRepository.existsByIdAndMyShoeBoxId(imageId, myShoeBoxId);
    }

    public void deleteById(Long imageId) {
        imageRepository.deleteById(imageId);
    }

    public void deleteAllById(List<Long> ids) {
        imageRepository.deleteAllById(ids);
    }

    public ImageData loadImageDataById(Long id) {
        Image image = loadById(id);
        ImageData imageData = imageDataService.constructImageData(image);
        return imageData;
    }

    public Image loadById(Long id) {
        Optional<Image> optionalImage = imageRepository.findById(id);
        Image image = optionalImage.isPresent() ? optionalImage.get() : null;
        return image;
    }

    public List<Long> loadImageIdsByMyShoeBoxId(Long myShoeBoxId) {
        List<Image> images = imageRepository.findAllByMyShoeBoxId(myShoeBoxId);
        List<Long> ids = images.stream().map(i -> i.getId()).collect(Collectors.toList());
        return ids;
    }

    public List<UserImageData> loadUserImagesByMyShoeBoxId(Long myShoeBoxId) {
        List<Image> images = imageRepository.findAllByMyShoeBoxId(myShoeBoxId);
        List<UserImageData> imageDatas = imageDataService.constructUserImageDatas(images);
        return imageDatas;
    }

    public int loadMyShoeBoxSize(Long myShoeBoxId) {
        int myShoeBoxSize = imageRepository.countByMyShoeBoxId(myShoeBoxId);
        return myShoeBoxSize;
    }

}
