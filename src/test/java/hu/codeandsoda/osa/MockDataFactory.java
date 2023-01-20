package hu.codeandsoda.osa;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;

import hu.codeandsoda.osa.account.user.data.UserData;
import hu.codeandsoda.osa.account.user.domain.User;
import hu.codeandsoda.osa.account.user.domain.UserDetail;
import hu.codeandsoda.osa.changepassword.data.ChangePasswordData;
import hu.codeandsoda.osa.general.data.ResponseId;
import hu.codeandsoda.osa.general.data.ResponseMessage;
import hu.codeandsoda.osa.general.data.ResponseMessageScope;
import hu.codeandsoda.osa.myshoebox.data.ImageData;
import hu.codeandsoda.osa.myshoebox.domain.Image;
import hu.codeandsoda.osa.myshoebox.domain.MyShoeBox;
import hu.codeandsoda.osa.registration.data.RegistrationData;
import hu.codeandsoda.osa.registration.data.RegistrationTokenData;
import hu.codeandsoda.osa.registration.domain.RegistrationToken;
import hu.codeandsoda.osa.resetpassword.data.RecoveryEmailData;
import hu.codeandsoda.osa.resetpassword.data.ResetPasswordData;
import hu.codeandsoda.osa.resetpassword.domain.PasswordResetToken;

public class MockDataFactory {

    public MockDataFactory() {
    }

    public static User getMockUserWithoutId() {
        UserDetail userDetail = new UserDetail.UserDetailBuilder().setName("Test User").build();
        userDetail.setId(1L);
        MyShoeBox myShoeBox = new MyShoeBox("myshoebox");
        User user = new User("test@test.com", "testPassword", userDetail, myShoeBox, "testDisplayName");
        return user;
    }

    public static User getMockUserWithId() {
        UserDetail userDetail = new UserDetail.UserDetailBuilder().setName("Test User").build();
        userDetail.setId(1L);
        MyShoeBox myShoeBox = new MyShoeBox("myshoebox");
        User user = new User("test@test.com", "testPassword", userDetail, myShoeBox, "testDisplayName");
        user.setId(1L);
        return user;
    }

    public static User getEnabledMockUser() {
        User user = getMockUserWithId();
        user.setEnabled(true);
        return user;
    }

    public static UserData getMockUserData() {
        UserData userData = new UserData.UserDataBuilder().setName("test@test.com").build();
        return userData;
    }

    public static RegistrationData getRegistrationData() {
        RegistrationData registrationData = new RegistrationData.RegistrationDataBuilder().setEmail("test@test.com").setPassword("testPassword").build();
        return registrationData;
    }

    public static Errors getRegistrationDataErrors() {
        Errors errors = new BeanPropertyBindingResult(getRegistrationData(), "registrationData");
        return errors;
    }

    public static RegistrationToken getRegistrationTokenWithId(User user) {
        RegistrationToken token = new RegistrationToken("token", user);
        token.setId(1L);
        return token;
    }

    public static RegistrationTokenData getRegistrationTokenData() {
        RegistrationTokenData tokenData = new RegistrationTokenData.RegistrationTokenDataBuilder().setToken("token").build();
        return tokenData;
    }

    public static ChangePasswordData getChangePasswordData() {
        ChangePasswordData changePasswordData = new ChangePasswordData.ChangePasswordDataBuilder().setNewPassword("new password").setOldPassword("old password").build();
        return changePasswordData;
    }

    public static PasswordResetToken getPasswordResetTokenWithoutId(User user, String token) {
        PasswordResetToken passwordResetToken = new PasswordResetToken(token, user);
        return passwordResetToken;
    }

    public static PasswordResetToken getPasswordResetTokenWithId(User user, String token) {
        PasswordResetToken passwordResetToken = getPasswordResetTokenWithoutId(user, token);
        passwordResetToken.setId(1L);
        return passwordResetToken;
    }

    public static RecoveryEmailData getRecoveryEmailData() {
        RecoveryEmailData recoveryEmailData = new RecoveryEmailData.RecoveryEmailDataBuilder().setEmail("recovery.test@test.hu").build();
        return recoveryEmailData;
    }

    public static ResetPasswordData getResetPasswordData(String token) {
        ResetPasswordData resetPasswordData = new ResetPasswordData.ResetPasswordDataBuilder().setToken(token).setPassword("new password").build();
        return resetPasswordData;
    }

    public static Image getImageWithoutId(ZonedDateTime uploadedAt) {
        Image image = new Image();
        image.setName("image.jpeg");
        image.setUrl("image.jpeg.com");
        image.setActiveUrl("active_image.jpeg.com");
        image.setThumbnailUrl("thumbnail_image.jpeg.com");
        image.setActiveThumbnailUrl("active_thumbnail_image.jpeg.com");
        image.setRotation(0);
        image.setUploadedAt(uploadedAt);
        image.setModifiedAt(uploadedAt);

        MyShoeBox myShoeBox = new MyShoeBox();
        myShoeBox.setName("myshoebox");
        image.setMyShoeBox(myShoeBox);

        return image;
    }

    public static Image getImageWithoutIdAndWithoutThumbnail(ZonedDateTime uploadedAt) {
        Image image = new Image();
        image.setName("image.jpeg");
        image.setUrl("image.jpeg.com");
        image.setActiveUrl("active_image.jpeg.com");
        image.setRotation(0);
        image.setUploadedAt(uploadedAt);
        image.setModifiedAt(uploadedAt);

        MyShoeBox myShoeBox = new MyShoeBox();
        myShoeBox.setName("myshoebox");
        image.setMyShoeBox(myShoeBox);

        return image;
    }

    public static Image getImageWithId(ZonedDateTime uploadedAt) {
        Image image = getImageWithId(1L, uploadedAt);
        return image;
    }

    public static Image getImageWithId(Long id, ZonedDateTime uploadedAt) {
        Image image = new Image();
        image.setId(id);
        image.setName("image.jpeg");
        image.setUrl("image.jpeg.com");
        image.setActiveUrl("active_image.jpeg.com");
        image.setThumbnailUrl("thumbnail_image.jpeg.com");
        image.setActiveThumbnailUrl("active_thumbnail_image.jpeg.com");
        image.setRotation(0);
        image.setUploadedAt(uploadedAt);
        image.setModifiedAt(uploadedAt);

        MyShoeBox myShoeBox = new MyShoeBox();
        myShoeBox.setId(1L);
        myShoeBox.setName("myshoebox");
        image.setMyShoeBox(myShoeBox);

        return image;
    }

    public static ImageData getImageData(ZonedDateTime uploadedAt) {
        ImageData imageData = getImageData(1L, uploadedAt);
        return imageData;
    }

    public static ImageData getImageData(Long id, ZonedDateTime uploadedAt) {
        ImageData imageData = new ImageData.ImageDataBuilder().setId(id).setName("image.jpeg").setUrl("image.jpeg.com").setActiveUrl("active_image.jpeg.com")
                .setThumbnailUrl("thumbnail_image.jpeg.com")
                .setActiveThumbnailUrl("active_thumbnail_image.jpeg.com").setRotation(0).setUploadedAt(uploadedAt).setModifiedAt(uploadedAt).build();
        return imageData;
    }

    public static ImageData getImageDataWithoutThumbnailUrl(ZonedDateTime uploadedAt) {
        ImageData imageData = new ImageData.ImageDataBuilder().setId(1L).setName("image.jpeg").setUrl("image.jpeg.com").setActiveUrl("active_image.jpeg.com")
                .setRotation(0).setUploadedAt(uploadedAt).setModifiedAt(uploadedAt).build();
        return imageData;
    }

    public static ImageData getImageDataWithThumbnailWarning(ZonedDateTime uploadedAt) {
        ResponseMessage message = new ResponseMessage.ResponseMessageBuilder().setId(ResponseId.THUMBNAIL_URL).setMessage("Could not generate thumbnail.")
                .setResponseMessageScope(ResponseMessageScope.WARNING)
                .build();
        List<ResponseMessage> messages = new ArrayList<>();
        messages.add(message);

        ImageData imageData = new ImageData.ImageDataBuilder().setId(1L).setName("image.jpeg").setUrl("image.jpeg.com").setActiveUrl("active_image.jpeg.com")
                .setThumbnailUrl("thumbnail_image.jpeg.com").setActiveThumbnailUrl("active_thumbnail_image.jpeg.com").setRotation(0)
                .setUploadedAt(uploadedAt).setModifiedAt(uploadedAt).setMessages(messages).build();
        return imageData;
    }

    public static MockMultipartFile getJpegMockMultiPartFile() {
        MockMultipartFile file = new MockMultipartFile("file", "test.txt", MediaType.IMAGE_JPEG_VALUE, new byte[0]);
        return file;
    }
}
