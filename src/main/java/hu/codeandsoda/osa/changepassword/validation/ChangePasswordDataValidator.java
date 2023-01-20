package hu.codeandsoda.osa.changepassword.validation;

import java.text.MessageFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import hu.codeandsoda.osa.account.user.domain.User;
import hu.codeandsoda.osa.changepassword.data.ChangePasswordData;
import hu.codeandsoda.osa.security.service.UserService;
import hu.codeandsoda.osa.util.ErrorCode;
import hu.codeandsoda.osa.util.OsaConstantUtil;

@Component
public class ChangePasswordDataValidator implements Validator {

    private static final String OLD_PASSWORD_FIELD = "oldPassword";
    private static final String NEW_PASSWORD_FIELD = "newPassword";
    private static final String REPEAT_NEW_PASSWORD_FIELD = "repeatNewPassword";

    private static final String OLD_NEW_PASSWORD_NOT_DIFFERENT = "Old and New Password should be different.";
    private static final String PASSWORDS_DO_NOT_MATCH = "Passwords do not match.";

    private static Logger logger = LoggerFactory.getLogger(ChangePasswordDataValidator.class);

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${osa.user.password.length.min}")
    private int passwordLengthMin;

    @Override
    public boolean supports(Class<?> clazz) {
        return ChangePasswordData.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        ChangePasswordData changePasswordData = (ChangePasswordData) target;
        String oldPassword = changePasswordData.getOldPassword();
        String newPassword = changePasswordData.getNewPassword();
        String repeatNewPassword = changePasswordData.getRepeatNewPassword();

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = user.getId();

        boolean isOldPasswordValid = validateOldPassword(oldPassword, errors, user);
        boolean isNewPasswordValid = validateNewPassword(newPassword, oldPassword, NEW_PASSWORD_FIELD, errors, userId);
        boolean isRepeatNewPasswordValid = validateNewPassword(repeatNewPassword, oldPassword, REPEAT_NEW_PASSWORD_FIELD, errors, userId);

        if (isOldPasswordValid && isNewPasswordValid && isRepeatNewPasswordValid) {
            validatePasswordMatch(newPassword, repeatNewPassword, errors, userId);
        }
    }

    private boolean validateOldPassword(String oldPassword, Errors errors, User user) {
        boolean isOldPasswordValid = true;

        boolean isOldPasswordEmpty = !StringUtils.hasText(oldPassword);
        if (isOldPasswordEmpty) {
            handleError(OLD_PASSWORD_FIELD, "Missing oldPassword input.", ErrorCode.INVALID_PASSWORD, errors, user.getId());
            isOldPasswordValid = false;
        } else if (!isOldPasswordValid(oldPassword, user)) {
            handleError(OLD_PASSWORD_FIELD, "Invalid password.", ErrorCode.INVALID_PASSWORD, errors, user.getId());
            isOldPasswordValid = false;
        }

        return isOldPasswordValid;
    }

    private boolean isOldPasswordValid(String oldPassword, User user) {
        return passwordEncoder.matches(oldPassword, user.getPassword());
    }

    private boolean validateNewPassword(String newPassword, String oldPassword, String field, Errors errors, Long userId) {
        boolean isNewPasswordValid = true;

        boolean isNewPasswordEmpty = !StringUtils.hasText(newPassword);
        if (isNewPasswordEmpty) {
            String errorMessage = MessageFormat.format("Missing input field : {0}.", field);
            handleError(field, errorMessage, ErrorCode.INVALID_NEW_PASSWORD, errors, userId);
            isNewPasswordValid = false;
        } else if (oldPassword.equals(newPassword)) {
            handleError(OLD_PASSWORD_FIELD, OLD_NEW_PASSWORD_NOT_DIFFERENT, ErrorCode.INVALID_NEW_PASSWORD, errors, userId);
            handleError(field, OLD_NEW_PASSWORD_NOT_DIFFERENT, ErrorCode.INVALID_NEW_PASSWORD, errors, userId);
            isNewPasswordValid = false;
        } else if (!userService.isPasswordLengthValid(newPassword)) {
            String errorMessage = MessageFormat.format("Password length is invalid. Minimum length is {0}", passwordLengthMin);
            handleError(field, errorMessage, ErrorCode.INVALID_PASSWORD_LENGTH, errors, userId);
            isNewPasswordValid = false;
        }

        return isNewPasswordValid;
    }

    private void validatePasswordMatch(String newPassword, String repeatNewPassword, Errors errors, Long userId) {
        if (!newPassword.equals(repeatNewPassword)) {
            handleError(NEW_PASSWORD_FIELD, PASSWORDS_DO_NOT_MATCH, ErrorCode.INVALID_NEW_PASSWORD, errors, userId);
            handleError(REPEAT_NEW_PASSWORD_FIELD, PASSWORDS_DO_NOT_MATCH, ErrorCode.INVALID_NEW_PASSWORD, errors, userId);
        }
    }

    private void handleError(String field, String message, ErrorCode errorCode, Errors errors, Long userId) {
        errors.rejectValue(field, OsaConstantUtil.TEMPLATE_FORM_EMPTY_ERROR_CODE, message);
        String logMessage = MessageFormat.format("action=changePassword, status=error, errorCode={0}, userId={1}", errorCode, userId);
        logger.error(logMessage);
    }

}
