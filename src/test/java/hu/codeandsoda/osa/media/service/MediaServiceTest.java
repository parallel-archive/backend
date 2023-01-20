package hu.codeandsoda.osa.media.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.net.URLConnection;
import java.time.ZonedDateTime;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.junit.jupiter.EnabledIf;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.multipart.MultipartFile;

import hu.codeandsoda.osa.MockDataFactory;
import hu.codeandsoda.osa.media.exception.MediaUploadException;
import hu.codeandsoda.osa.myshoebox.data.ImageData;
import hu.codeandsoda.osa.myshoebox.service.ImageService;

//@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@EnabledIf(value = "#{'${spring.profiles.active}' == 'unittest'}", loadContext = true)
public class MediaServiceTest {

    @Autowired
    private MediaService mediaService;

    @MockBean
    private AwsS3Service awsS3Service;

    @MockBean
    private ThumbnailService thumbnailService;

    @MockBean
    private ImageService imageService;

    @BeforeAll
    public static void init() throws IOException {
        mockStatic(URLConnection.class);
        when(URLConnection.guessContentTypeFromStream(Mockito.any())).thenReturn("image/jpeg");
    }


    @Test
    @WithUserDetails("test@test.hu")
    @DisplayName("Test save image and return image data with image and thumbnail url")
    void givenImageAndThumbnailIsSavedWhenuploadImageWithThumbnailThenReturnsImageData() throws MediaUploadException, IOException {
        ZonedDateTime uploadedAt = ZonedDateTime.now();
        ImageData expected = MockDataFactory.getImageData(uploadedAt);
        MultipartFile jpeg = MockDataFactory.getJpegMockMultiPartFile();

        when(awsS3Service.upload(Mockito.any(), Mockito.anyString(), Mockito.anyString(), Mockito.anyLong(), Mockito.any())).thenReturn(expected.getUrl());
        when(thumbnailService.generateThumbnail(jpeg, Mockito.anyLong(), Mockito.any())).thenReturn(jpeg.getInputStream());
        when(awsS3Service.upload(Mockito.any(), Mockito.anyString(), Mockito.anyString(), Mockito.anyLong(), Mockito.any())).thenReturn(expected.getThumbnailUrl());
        when(imageService.save(Mockito.any())).thenReturn(expected);

        Errors errors = new BeanPropertyBindingResult(jpeg, "jpeg");
        ImageData result = mediaService.uploadImageWithThumbnail(jpeg, errors);

        assertEquals(expected, result);
    }

    @Test
    @WithUserDetails("test@test.hu")
    @DisplayName("Test save image without thumbnail and return image data with warning")
    void givenImageIsSavedWithoutThumbnailWhenuploadImageWithThumbnailThenReturnsImageDataWithWarning() throws MediaUploadException, IOException {
        ZonedDateTime uploadedAt = ZonedDateTime.now();
        ImageData imageData = MockDataFactory.getImageDataWithoutThumbnailUrl(uploadedAt);

        when(awsS3Service.upload(Mockito.any(), Mockito.anyString(), Mockito.anyString(), Mockito.anyLong(), Mockito.any())).thenReturn(imageData.getUrl());
        when(imageService.save(Mockito.any())).thenReturn(imageData);

        ImageData expected = MockDataFactory.getImageDataWithThumbnailWarning(uploadedAt);

        MultipartFile jpeg = MockDataFactory.getJpegMockMultiPartFile();
        Errors errors = new BeanPropertyBindingResult(jpeg, "jpeg");
        ImageData result = mediaService.uploadImageWithThumbnail(jpeg, errors);

        assertEquals(expected, result);
    }

}
