package hu.codeandsoda.osa.resetpassword.validation;

import java.text.MessageFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import hu.codeandsoda.osa.resetpassword.data.ResetPasswordData;
import hu.codeandsoda.osa.resetpassword.service.PasswordResetTokenService;
import hu.codeandsoda.osa.security.service.UserService;
import hu.codeandsoda.osa.util.ErrorCode;
import hu.codeandsoda.osa.util.OsaConstantUtil;

@Component
public class ResetPasswordDataValidator implements Validator {

    private static final String TOKEN_FIELD = "token";
    private static final String PASSWORD_FIELD = "password";
    private static final String REPEAT_PASSWORD_FIELD = "repeatPassword";

    private static final String PASSWORDS_DO_NOT_MATCH = "Passwords do not match.";

    private static Logger logger = LoggerFactory.getLogger(ResetPasswordDataValidator.class);

    @Autowired
    private PasswordResetTokenService passwordResetTokenService;

    @Autowired
    private UserService userService;

    @Value("${osa.user.password.length.min}")
    private int passwordLengthMin;

    @Override
    public boolean supports(Class<?> clazz) {
        return ResetPasswordData.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        ResetPasswordData resetPasswordData = (ResetPasswordData) target;
        String token = resetPasswordData.getToken();
        String password = resetPasswordData.getPassword();
        String repeatPassword = resetPasswordData.getRepeatPassword();

        validateToken(token, errors);
        validatePasswords(password, repeatPassword, errors);
    }

    private void validateToken(String token, Errors errors) {
        if (null == token || !passwordResetTokenService.tokenExists(token)) {
            String message = "Invalid token.";
            errors.rejectValue(TOKEN_FIELD, OsaConstantUtil.TEMPLATE_FORM_EMPTY_ERROR_CODE, message);
            String logMessage = MessageFormat.format("action=resetPassword, status=error, errorCode={0}, token={1}", ErrorCode.INVALID_TOKEN, token);
            logger.error(logMessage);
        }
    }

    private void validatePasswords(String password, String repeatPassword, Errors errors) {
        boolean isPasswordValid = isPasswordValid(password, PASSWORD_FIELD, errors);
        boolean isRepeatPasswordValid = isPasswordValid(repeatPassword, REPEAT_PASSWORD_FIELD, errors);

        if (isPasswordValid && isRepeatPasswordValid) {
            validatePasswordMatch(password, repeatPassword, errors);
        }
    }

    private boolean isPasswordValid(String password, String field, Errors errors) {
        boolean isPasswordValid = true;
        if (!StringUtils.hasText(password)) {
            String errorMessage = MessageFormat.format("Missing input field : {0}.", field);
            handlePasswordError(field, errorMessage, ErrorCode.INVALID_PASSWORD, errors);
            isPasswordValid = false;
        } else if (!userService.isPasswordLengthValid(password)) {
            String errorMessage = MessageFormat.format("Password length is invalid. Minimum length is {0}", passwordLengthMin);
            handlePasswordError(field, errorMessage, ErrorCode.INVALID_PASSWORD_LENGTH, errors);
            isPasswordValid = false;
        }
        return isPasswordValid;
    }

    private void validatePasswordMatch(String password, String repeatPassword, Errors errors) {
        if (!password.equals(repeatPassword)) {
            handlePasswordError(PASSWORD_FIELD, PASSWORDS_DO_NOT_MATCH, ErrorCode.PASSWORDS_DO_NOT_MATCH, errors);
            handlePasswordError(REPEAT_PASSWORD_FIELD, PASSWORDS_DO_NOT_MATCH, ErrorCode.PASSWORDS_DO_NOT_MATCH, errors);
        }
    }

    private void handlePasswordError(String field, String message, ErrorCode errorCode, Errors errors) {
        errors.rejectValue(field, OsaConstantUtil.TEMPLATE_FORM_EMPTY_ERROR_CODE, message);
        String logMessage = MessageFormat.format("action=resetPassword, status=error, errorCode={0}, message={1}", errorCode, message);
        logger.error(logMessage);
    }

}
