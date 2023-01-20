package hu.codeandsoda.osa.myshoebox.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.EnabledIf;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import hu.codeandsoda.osa.MockDataFactory;
import hu.codeandsoda.osa.myshoebox.data.ImageData;
import hu.codeandsoda.osa.myshoebox.domain.Image;
import hu.codeandsoda.osa.myshoebox.repository.ImageRepository;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@EnabledIf(value = "#{'${spring.profiles.active}' == 'unittest'}", loadContext = true)
public class ImageDataServiceTest {

    @Autowired
    private ImageDataService imageDataService;

    @MockBean
    private ImageRepository imageRepository;

    @Test
    @DisplayName("Test image to image data conversion")
    void givenImageListWhenConstructImageDatasThenReturnsImageDataList() {
        ZonedDateTime uploadedAt = ZonedDateTime.now();
        List<Image> images = new ArrayList<>();
        images.add(MockDataFactory.getImageWithId(1L, uploadedAt));
        images.add(MockDataFactory.getImageWithId(2L, uploadedAt));

        List<ImageData> expected = new ArrayList<>();
        expected.add(MockDataFactory.getImageData(1L, uploadedAt));
        expected.add(MockDataFactory.getImageData(2L, uploadedAt));

        List<ImageData> result = imageDataService.constructImageDatas(images);
        assertEquals(expected, result);
    }

}
