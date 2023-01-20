package hu.codeandsoda.osa.registration.service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.DatatypeConverter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.SerializationUtils;
import org.springframework.validation.Errors;

import hu.codeandsoda.osa.account.user.data.UserData;
import hu.codeandsoda.osa.account.user.domain.User;
import hu.codeandsoda.osa.email.data.EmailParams;
import hu.codeandsoda.osa.email.data.EmailTemplate;
import hu.codeandsoda.osa.jms.service.email.EmailMessageService;
import hu.codeandsoda.osa.myshoebox.domain.MyShoeBox;
import hu.codeandsoda.osa.registration.data.RegistrationData;
import hu.codeandsoda.osa.registration.data.RegistrationTokenData;
import hu.codeandsoda.osa.registration.exception.RegistrationException;
import hu.codeandsoda.osa.security.service.UserService;
import hu.codeandsoda.osa.util.ErrorCode;

@Service
public class RegistrationService {

    private static Logger logger = LoggerFactory.getLogger(RegistrationService.class);

    @Autowired
    private UserService userService;

    @Autowired
    private RegistrationTokenService registrationTokenService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailMessageService emailMessageService;

    @Value("${osa.baseUrl}")
    private String baseUrl;

    public UserData registerUser(RegistrationData registrationData, Errors errors) throws RegistrationException {
        User user = createUserFromRegistrationData(registrationData, errors);
        User savedUser = userService.save(user);

        UserData userData = new UserData();
        if (null == savedUser) {
            String errorCode = ErrorCode.USER_NOT_SAVED.toString();
            errors.reject(errorCode, "Could not register user.");
            String errorLogMessage = MessageFormat.format("action=registration, status=error, email={0}, errorCode={1}", registrationData.getEmail(), errorCode);
            logger.error(errorLogMessage);
        } else {
            String logMessage = MessageFormat.format("action=registration, status=success, userId={0}", savedUser.getId());
            logger.info(logMessage);
            userData = new UserData.UserDataBuilder().setName(savedUser.getEmail()).build();
        }
        return userData;
    }

    public void sendConfirmRegistrationEmail(UserData user) {
        User savedUser = userService.findByEmail(user.getName());
        String token = registrationTokenService.createRegistrationToken(savedUser);
        String tokenUrl = new StringBuilder().append(baseUrl).append("/confirm-register/").append(token).toString();

        Map<String, String> replacements = new HashMap<>();
        replacements.put("user", savedUser.getUsername());
        replacements.put("token", tokenUrl);

        EmailTemplate htmlTemplate = new EmailTemplate("confirm_registration.html");
        EmailTemplate textTemplate = new EmailTemplate("confirm_registration.txt");
        String htmlMessage = htmlTemplate.getTemplate(replacements);
        String textMessage = textTemplate.getTemplate(replacements);

        EmailParams emailParams = new EmailParams.EmailParamsBuilder().setTo(savedUser.getEmail()).setSubject("Registration Confirmation").setHtmlMessage(htmlMessage)
                .setTextMessage(textMessage).setUserId(savedUser.getId()).build();

        emailMessageService.send(emailParams);
    }

    public UserData enableUser(RegistrationTokenData registrationToken) {
        User user = registrationTokenService.loadUserByToken(registrationToken.getToken());
        User enabledUser = userService.enableUser(user);

        String logMessage = MessageFormat.format("action=registrationValidation, status=success, userId={0}", enabledUser.getId());
        logger.info(logMessage);
        return new UserData.UserDataBuilder().setName(enabledUser.getEmail()).build();
    }

    private User createUserFromRegistrationData(RegistrationData registrationData, Errors errors) throws RegistrationException {
        MyShoeBox myShoeBox = new MyShoeBox("myshoebox");
        String email = registrationData.getEmail();

        User user = new User();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(registrationData.getPassword()));
        user.setMyShoeBox(myShoeBox);
        user.setDisplayName(generateUserDisplayName(email, errors));

        return user;
    }

    private String generateUserDisplayName(String email, Errors errors) throws RegistrationException {
        String displayName = null;
        try {
            byte[] data = SerializationUtils.serialize(email);
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedhash = digest.digest(data);
            displayName = DatatypeConverter.printHexBinary(encodedhash);
        } catch (NoSuchAlgorithmException e) {
            String errorLogMessage = MessageFormat.format("action=registration, status=error, email={0}, errorCode={1}", email, e.getMessage());
            logger.error(errorLogMessage);
            errors.reject(ErrorCode.USER_NOT_SAVED.toString(), "Could not generate user display name.");
            throw new RegistrationException("Could not generate user display name.", errors.getAllErrors());
        }
        return displayName;
    }

}
