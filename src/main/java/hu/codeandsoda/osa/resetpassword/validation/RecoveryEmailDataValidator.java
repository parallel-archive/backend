package hu.codeandsoda.osa.resetpassword.validation;

import java.text.MessageFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import hu.codeandsoda.osa.account.user.domain.User;
import hu.codeandsoda.osa.resetpassword.data.RecoveryEmailData;
import hu.codeandsoda.osa.security.service.UserService;
import hu.codeandsoda.osa.util.ErrorCode;

@Component
public class RecoveryEmailDataValidator implements Validator {

    private static Logger logger = LoggerFactory.getLogger(RecoveryEmailDataValidator.class);

    @Autowired
    private UserService userService;

    @Override
    public boolean supports(Class<?> clazz) {
        return RecoveryEmailData.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        RecoveryEmailData recoveryEmailData = (RecoveryEmailData) target;
        String email = recoveryEmailData.getEmail();

        if (!userService.userExists(email)) {
            errors.reject(ErrorCode.SUBMIT_FORGOTTEN_PASSWORD_REQUEST_ERROR.toString(), "Could not submit forgotten password request.");
            String logMessage = MessageFormat.format("action=passwordRecovery, status=error, error={0}, email={1}", "There is no User registrated with this e-mail address.", email);
            logger.error(logMessage);
        }

        if (null != email && userService.userExists(email)) {
            User user = userService.findByEmail(email);
            if (!user.isEnabled()) {
                errors.reject(ErrorCode.SUBMIT_FORGOTTEN_PASSWORD_REQUEST_ERROR.toString(), "Could not submit forgotten password request.");
                String logMessage = MessageFormat.format("action=passwordRecovery, status=error, error={0}, email={1}, userId={2}",
                        "Can not reset User password. User is not enabled yet.", email, user.getId());
                logger.error(logMessage);
            }
        }
    }

}
