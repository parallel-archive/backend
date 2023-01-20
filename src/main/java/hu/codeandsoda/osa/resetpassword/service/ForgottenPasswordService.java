package hu.codeandsoda.osa.resetpassword.service;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import hu.codeandsoda.osa.account.user.domain.User;
import hu.codeandsoda.osa.email.data.EmailParams;
import hu.codeandsoda.osa.email.data.EmailTemplate;
import hu.codeandsoda.osa.jms.service.email.EmailMessageService;
import hu.codeandsoda.osa.resetpassword.data.RecoveryEmailData;
import hu.codeandsoda.osa.resetpassword.data.ResetPasswordData;
import hu.codeandsoda.osa.resetpassword.web.ForgottenPasswordController;
import hu.codeandsoda.osa.security.service.UserService;

@Service
public class ForgottenPasswordService {

    private static final String RECOVER_PASSWORD_EMAIL_SUBJECT = "Forgotten Password";
    private static final String RESET_PASSWORD_EMAIL_SUBJECT = "Your password has been changed";

    private static Logger logger = LoggerFactory.getLogger(ForgottenPasswordService.class);

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordResetTokenService passwordResetTokenService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailMessageService emailMessageService;

    @Value("${osa.baseUrl}")
    private String baseUrl;

    public void recoverPassword(RecoveryEmailData recoveryEmail) {
        User user = userService.findByEmail(recoveryEmail.getEmail());

        if (null != user) {
            String token = passwordResetTokenService.createPasswordResetToken(user);
            String tokenUrl = new StringBuilder().append(baseUrl).append(ForgottenPasswordController.RESET_PASSWORD_SUBMIT_URL).append("/").append(token)
                    .toString();

            EmailTemplate htmlTemplate = new EmailTemplate("recover_password.html");
            EmailTemplate textTemplate = new EmailTemplate("recover_password.txt");

            Map<String, String> replacements = new HashMap<>();
            replacements.put("user", user.getUsername());
            replacements.put("token", tokenUrl);

            String htmlMessage = htmlTemplate.getTemplate(replacements);
            String textMessage = textTemplate.getTemplate(replacements);

            EmailParams emailParams = new EmailParams.EmailParamsBuilder().setTo(user.getEmail()).setSubject(RECOVER_PASSWORD_EMAIL_SUBJECT).setHtmlMessage(htmlMessage)
                    .setTextMessage(textMessage).setUserId(user.getId())
                    .build();

            emailMessageService.send(emailParams);
        }
    }

    public void resetPassword(ResetPasswordData resetPasswordData) {

        User user = passwordResetTokenService.loadUserByToken(resetPasswordData.getToken());
        user.setPassword(passwordEncoder.encode(resetPasswordData.getPassword()));
        User savedUser = userService.save(user);

        if (null != savedUser) {
            String loginUrl = new StringBuilder().append(baseUrl).append("/login").toString();

            EmailTemplate htmlTemplate = new EmailTemplate("recovered_password.html");
            EmailTemplate textTemplate = new EmailTemplate("recovered_password.txt");

            Map<String, String> replacements = new HashMap<>();
            replacements.put("user", savedUser.getUsername());
            replacements.put("login", loginUrl);

            String htmlMessage = htmlTemplate.getTemplate(replacements);
            String textMessage = textTemplate.getTemplate(replacements);

            EmailParams emailParams = new EmailParams.EmailParamsBuilder().setTo(savedUser.getEmail()).setSubject(RESET_PASSWORD_EMAIL_SUBJECT).setHtmlMessage(htmlMessage)
                    .setTextMessage(textMessage).setUserId(savedUser.getId())
                    .build();
            emailMessageService.send(emailParams);

            String logMessage = new StringBuilder().append("action=resetPassword, status=success, userId=").append(user.getId()).toString();
            logger.info(logMessage);
        }

    }

}
