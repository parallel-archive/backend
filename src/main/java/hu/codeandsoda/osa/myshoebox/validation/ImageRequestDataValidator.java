package hu.codeandsoda.osa.myshoebox.validation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import hu.codeandsoda.osa.account.user.domain.User;
import hu.codeandsoda.osa.myshoebox.data.ImageRequestData;
import hu.codeandsoda.osa.myshoebox.data.Rotation;
import hu.codeandsoda.osa.myshoebox.domain.Image;
import hu.codeandsoda.osa.myshoebox.service.ImageService;
import hu.codeandsoda.osa.util.ErrorCode;

@Component
public class ImageRequestDataValidator implements Validator {

    private static Logger logger = LoggerFactory.getLogger(ImageRequestDataValidator.class);

    @Autowired
    private ImageService imageService;

    @Override
    public boolean supports(Class<?> clazz) {
        return ImageRequestData.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        ImageRequestData imageRequestData = (ImageRequestData) target;
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long myShoeBoxId = user.getMyShoeBox().getId();

        Long imageId = imageRequestData.getId();
        Image image = imageService.loadById(imageId);
        boolean imageExists = null != image;
        if (!imageExists) {
            String errorCode = ErrorCode.INVALID_IMAGE_ID.toString();
            String logMessage = new StringBuilder().append("action=editImage, status=error, userId=").append(user.getId()).append(", imageId=").append(imageId)
                    .append(", errorCode=").append(errorCode).toString();
            errors.reject(errorCode, "Invalid image id: " + imageId);
            logger.error(logMessage);
        } else if (!image.getMyShoeBox().getId().equals(myShoeBoxId)) {
            String errorCode = ErrorCode.INVALID_IMAGE_ID.toString();
            String logMessage = new StringBuilder().append("action=editImage, status=error, userId=").append(user.getId()).append(", myShoeBoxId=").append(myShoeBoxId)
                    .append(", imageMyShoeBoxId=").append(image.getMyShoeBox().getId()).append(", imageId=").append(imageId).append(", errorCode=").append(errorCode).toString();
            errors.reject(errorCode, "Invalid image id: " + imageId);
            logger.error(logMessage);
        }

        if (null != imageRequestData.getName() && !StringUtils.hasText(imageRequestData.getName())) {
            String errorCode = ErrorCode.IMAGE_NAME_EMPTY.toString();
            String logMessage = new StringBuilder().append("action=editImage, status=error, userId=").append(user.getId()).append(", imageId=").append(imageId)
                    .append(", errorCode=").append(errorCode).toString();
            errors.reject(errorCode, "Image name is empty.");
            logger.error(logMessage);
        }

        Integer rotation = imageRequestData.getRotation();
        if (null != rotation) {
            boolean validRotationValue = Rotation.isRotationValueValid(rotation);

            if (!validRotationValue) {
                String errorCode = ErrorCode.INVALID_IMAGE_ROTATION_VALUE.toString();
                String logMessage = new StringBuilder().append("action=editImage, status=error, userId=").append(user.getId()).append(", imageId=").append(imageId)
                        .append(", rotation=").append(rotation).append(", errorCode=").append(errorCode).toString();
                errors.reject(errorCode, "Invalid image rotation value: " + rotation);
                logger.error(logMessage);
            }
        }

    }


}
