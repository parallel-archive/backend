package hu.codeandsoda.osa.media.validation;

import java.text.MessageFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.multipart.MultipartFile;

import hu.codeandsoda.osa.account.user.domain.User;
import hu.codeandsoda.osa.media.service.MediaService;
import hu.codeandsoda.osa.media.service.MultipartFileService;
import hu.codeandsoda.osa.util.ErrorCode;

@Component
public class MultipartFileValidator implements Validator {

    private static Logger logger = LoggerFactory.getLogger(MultipartFileValidator.class);

    @Autowired
    private MultipartFileService multipartFileService;

    @Override
    public boolean supports(Class<?> clazz) {
        return MultipartFile.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        MultipartFile file = (MultipartFile) target;
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = user.getId();

        String fileContentType = multipartFileService.getFileContentType(file, userId, errors);

        validateFileContentType(fileContentType, userId, file.getOriginalFilename(), errors);
    }

    private void validateFileContentType(String fileContentType, Long userId, String fileName, Errors errors) {
        boolean validExtension = StringUtils.hasText(fileContentType) && MediaService.VALID_EXTENSIONS.contains(fileContentType);
        if (!validExtension) {
            String errorCode = ErrorCode.INVALID_MEDIA_CONTENT_TYPE.toString();
            String logMessage = MessageFormat.format("action=uploadMediaToAwsS3, status=error, userId={0}, media={1}, errorCode={2}, type={3}", userId, fileName, errorCode,
                    fileContentType);
            errors.reject(errorCode, "Invalid file content type : " + fileContentType);
            logger.error(logMessage);
        }
    }

}
