package hu.codeandsoda.osa.resetpassword.web;

import java.text.MessageFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import hu.codeandsoda.osa.resetpassword.service.PasswordResetTokenService;
import hu.codeandsoda.osa.util.ErrorCode;
import hu.codeandsoda.osa.util.OsaConstantUtil;

@Component
public class ResetPasswordTokenValidator implements Validator {

    private static Logger logger = LoggerFactory.getLogger(ResetPasswordTokenValidator.class);

    @Autowired
    private PasswordResetTokenService passwordResetTokenService;

    @Override
    public boolean supports(Class<?> clazz) {
        return String.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        String resetPasswordToken = (String) target;

        if (null == resetPasswordToken || !passwordResetTokenService.tokenExists(resetPasswordToken)) {
            String errorCode = ErrorCode.INVALID_TOKEN.toString();
            errors.rejectValue("token", OsaConstantUtil.TEMPLATE_FORM_EMPTY_ERROR_CODE, "Invalid token.");

            String logMessage = MessageFormat.format("action=resetPassword, status=error, errorCode={0}, token={1}", errorCode, resetPasswordToken);
            logger.error(logMessage);

        }
    }

}
