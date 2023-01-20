package hu.codeandsoda.osa.registration.validation;

import java.text.MessageFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import hu.codeandsoda.osa.registration.data.RegistrationData;
import hu.codeandsoda.osa.security.service.UserService;
import hu.codeandsoda.osa.util.ErrorCode;
import hu.codeandsoda.osa.util.OsaConstantUtil;

@Component
public class RegistrationDataValidator implements Validator {

    private static Logger logger = LoggerFactory.getLogger(RegistrationDataValidator.class);

    private static final String EMAIL_FIELD = "email";
    private static final String PASSWORD_FIELD = "password";
    private static final String REPEAT_PASSWORD_FIELD = "repeatPassword";

    private static final String PASSWORDS_DO_NOT_MATCH = "Passwords do not match.";

    @Autowired
    private UserService userService;

    @Value("${osa.user.password.length.min}")
    private int passwordLengthMin;

    @Override
    public boolean supports(Class<?> clazz) {
        return RegistrationData.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        RegistrationData registrationData = (RegistrationData) target;
        String email = registrationData.getEmail();
        String password = registrationData.getPassword();
        String repeatPassword = registrationData.getRepeatPassword();
        
        validateEmail(email, errors);
        validatePasswords(password, repeatPassword, errors);
    }

    private void validateEmail(String email, Errors errors) {
        if (!StringUtils.hasText(email)) {
            handleError(EMAIL_FIELD, "Missing e-mail input.", ErrorCode.INVALID_REGISTRATION_DATA, errors);
        } else if (userService.userExists(email)) {
            handleGlobalError("Error during registration.", ErrorCode.USER_EXISTS, errors);
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
            handleError(field, errorMessage, ErrorCode.INVALID_REGISTRATION_DATA, errors);
            isPasswordValid = false;
        } else if (!userService.isPasswordLengthValid(password)) {
            String errorMessage = MessageFormat.format("Password length is invalid. Minimum length is {0}", passwordLengthMin);
            handleError(field, errorMessage, ErrorCode.INVALID_PASSWORD_LENGTH, errors);
            isPasswordValid = false;
        }
        return isPasswordValid;
    }

    private void validatePasswordMatch(String password, String repeatPassword, Errors errors) {
        if (!password.equals(repeatPassword)) {
            handleError(PASSWORD_FIELD, PASSWORDS_DO_NOT_MATCH, ErrorCode.PASSWORDS_DO_NOT_MATCH, errors);
            handleError(REPEAT_PASSWORD_FIELD, PASSWORDS_DO_NOT_MATCH, ErrorCode.PASSWORDS_DO_NOT_MATCH, errors);
        }
    }

    private void handleError(String field, String message, ErrorCode errorCode, Errors errors) {
        errors.rejectValue(field, OsaConstantUtil.TEMPLATE_FORM_EMPTY_ERROR_CODE, message);
        String logMessage = MessageFormat.format("action=registration, status=error, errorCode={0}, message={1}", errorCode, message);
        logger.error(logMessage);
    }

    private void handleGlobalError(String message, ErrorCode errorCode, Errors errors) {
        errors.reject(ErrorCode.REGISTRATION_ERROR.toString(), message);
        String logMessage = MessageFormat.format("action=registration, status=error, errorCode={0}, message={1}", errorCode, message);
        logger.error(logMessage);
    }

}
