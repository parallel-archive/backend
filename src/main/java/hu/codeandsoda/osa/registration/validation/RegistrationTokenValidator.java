package hu.codeandsoda.osa.registration.validation;

import java.text.MessageFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import hu.codeandsoda.osa.account.user.domain.User;
import hu.codeandsoda.osa.registration.data.RegistrationTokenData;
import hu.codeandsoda.osa.registration.service.RegistrationTokenService;
import hu.codeandsoda.osa.util.ErrorCode;
import hu.codeandsoda.osa.util.OsaConstantUtil;

@Component
public class RegistrationTokenValidator implements Validator {

    private static Logger logger = LoggerFactory.getLogger(RegistrationTokenValidator.class);

    @Autowired
    private RegistrationTokenService registrationTokenService;

    public static final String INVALID_TOKEN_MESSAGE = "User token is not valid";
    public static final String USER_ALREADY_ENABLED_MESSAGE = "User is already enabled.";

    @Override
    public boolean supports(Class<?> clazz) {
        return RegistrationTokenData.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        RegistrationTokenData registrationToken = (RegistrationTokenData) target;
        String token = registrationToken.getToken();

        if (null == token || !registrationTokenService.tokenExists(token)) {
            errors.rejectValue("token", OsaConstantUtil.TEMPLATE_FORM_EMPTY_ERROR_CODE, INVALID_TOKEN_MESSAGE);
            String logMessage = MessageFormat.format("action=registrationValidation, status=error, errorCode={0}", ErrorCode.INVALID_TOKEN);
            logger.error(logMessage);
        }

        User user = registrationTokenService.loadUserByToken(token);
        if (null != user && user.isEnabled()) {
            final String errorCode = ErrorCode.USER_ALREADY_ENABLED.toString();
            errors.reject(errorCode, USER_ALREADY_ENABLED_MESSAGE);
            String logMessage = MessageFormat.format("action=registration, status=error, errorCode={0}", errorCode);
            logger.error(logMessage);
        }
    }

}
