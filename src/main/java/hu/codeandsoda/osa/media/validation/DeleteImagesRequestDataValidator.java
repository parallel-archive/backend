package hu.codeandsoda.osa.media.validation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import hu.codeandsoda.osa.account.user.domain.User;
import hu.codeandsoda.osa.myshoebox.data.DeleteImagesRequestData;
import hu.codeandsoda.osa.myshoebox.domain.Image;
import hu.codeandsoda.osa.myshoebox.service.ImageService;
import hu.codeandsoda.osa.util.ErrorCode;

@Component
public class DeleteImagesRequestDataValidator implements Validator {

    private static Logger logger = LoggerFactory.getLogger(DeleteImagesRequestDataValidator.class);

    @Autowired
    private ImageService imageService;

    @Override
    public boolean supports(Class<?> clazz) {
        return DeleteImagesRequestData.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        DeleteImagesRequestData deleteImagesRequestData = (DeleteImagesRequestData) target;
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long myShoeBoxId = user.getMyShoeBox().getId();

        for (Long imageId : deleteImagesRequestData.getImageIds()) {
            Image image = imageService.loadById(imageId);
            boolean imageExists = null != image;
            if (!imageExists || !myShoeBoxId.equals(image.getMyShoeBox().getId())) {
                String errorCode = ErrorCode.INVALID_IMAGE_ID.toString();
                String logMessage = new StringBuilder().append("action=deleteImages, status=error, userId=").append(user.getId()).append(", myShoeBoxId=").append(myShoeBoxId)
                        .append(", imageId=").append(imageId).append(", errorCode=").append(errorCode).toString();
                errors.reject(errorCode, "Invalid image ID.");
                logger.error(logMessage);
            }
        }
    }

}
