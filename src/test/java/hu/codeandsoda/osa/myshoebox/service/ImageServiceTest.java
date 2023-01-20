package hu.codeandsoda.osa.myshoebox.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.ZonedDateTime;
import java.util.Optional;

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
class ImageServiceTest {

    @Autowired
    private ImageService imageService;

    @MockBean
    private ImageRepository imageRepository;

    @Test
    @DisplayName("Test save image returns image data")
    void givenImageIsSavedWhenSaveImageThenReturnsImageData() {
        ZonedDateTime uploadedAt = ZonedDateTime.now();
        Image image = MockDataFactory.getImageWithoutId(uploadedAt);
        Image savedImage = MockDataFactory.getImageWithId(uploadedAt);
        when(imageRepository.save(image)).thenReturn(savedImage);
        
        ImageData expected = MockDataFactory.getImageData(uploadedAt);
        ImageData result = imageService.save(image);
        assertEquals(expected, result);
    }

    @Test
    @DisplayName("Test user's image exists returns true")
    void givenImageExistsAndBelongsToUserWhenUserImageExistsThenReturnsTrue() {
        Long imageId = 1L;
        Long myShoeBoxId = 1L;
        when(imageRepository.existsByIdAndMyShoeBoxId(imageId, myShoeBoxId)).thenReturn(true);
        assertTrue(imageService.userImageExists(imageId, myShoeBoxId));
    }

    @Test
    @DisplayName("Test user's image exists returns false")
    void givenImageExistsAndBelongsToUserWhenUserImageExistsThenReturnsFalse() {
        Long imageId = 1L;
        Long myShoeBoxId = 1L;
        when(imageRepository.existsByIdAndMyShoeBoxId(imageId, myShoeBoxId)).thenReturn(false);
        assertFalse(imageService.userImageExists(imageId, myShoeBoxId));
    }

    @Test
    @DisplayName("Test delete image by id is called")
    void givenTestDeleteImageIsCalledWhenDeleteByIdThenMehodIsCalled() {
        Long imageId = 1L;
        imageService.deleteById(imageId);
        verify(imageRepository, times(1)).deleteById(imageId);
    }

    @Test
    @DisplayName("Test load image by id returns image")
    void givenImageExistsWhenLoadByIdThenReturnsImage() {
        Long imageId = 1L;
        ZonedDateTime uploadedAt = ZonedDateTime.now();
        Image expected = MockDataFactory.getImageWithId(uploadedAt);
        when(imageRepository.findById(imageId)).thenReturn(Optional.of(expected));
        
        Image result = imageService.loadById(imageId);
        assertEquals(expected, result);
    }

    @Test
    @DisplayName("Test load image by id returns null")
    void givenImageDoesNotExistWhenLoadByIdThenReturnsNull() {
        Long imageId = 1L;
        when(imageRepository.findById(imageId)).thenReturn(Optional.empty());
        assertNull(imageService.loadById(imageId));
    }

}
