package hu.codeandsoda.osa.media.service;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.web.multipart.MultipartFile;

import net.coobird.thumbnailator.Thumbnails;

@Service
public class ThumbnailService {

    public static final String THUMBNAIL_EXTENSION = "png";

    @Autowired
    private MultipartFileService multipartFileService;

    public InputStream generateThumbnail(MultipartFile file, Long userId, Errors errors) throws IOException {
        InputStream is = new ByteArrayInputStream(new byte[0]);
        try (InputStream fileInputStream = multipartFileService.getFileInputStream(file, userId, errors); ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            BufferedImage thumbnail = Thumbnails.of(fileInputStream).size(640, 480).asBufferedImage();
            ImageIO.write(thumbnail, THUMBNAIL_EXTENSION, os);
            is = new ByteArrayInputStream(os.toByteArray());
        }
        return is;
    }

}
