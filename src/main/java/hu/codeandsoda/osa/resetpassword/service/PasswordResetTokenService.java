package hu.codeandsoda.osa.resetpassword.service;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import hu.codeandsoda.osa.account.user.domain.User;
import hu.codeandsoda.osa.resetpassword.domain.PasswordResetToken;
import hu.codeandsoda.osa.resetpassword.repository.PasswordResetTokenRepository;

@Service
public class PasswordResetTokenService {

    private static Logger logger = LoggerFactory.getLogger(PasswordResetTokenService.class);

    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;

    public String createPasswordResetToken(User user) {
        String token = UUID.randomUUID().toString();

        PasswordResetToken passwordResetToken = findPasswordResetTokenByUser(user);
        if (null == passwordResetToken) {
            passwordResetToken = new PasswordResetToken();
            passwordResetToken.setUser(user);
        }

        passwordResetToken.setToken(token);
        PasswordResetToken savedToken = save(passwordResetToken);

        String logMessage = new StringBuilder().append("action=generatePasswordResetToken, status=success, userId: ").append(user.getId()).toString();
        logger.info(logMessage);

        return savedToken.getToken();
    }

    private PasswordResetToken findPasswordResetTokenByUser(User user) {
        return passwordResetTokenRepository.findByUser(user);
    }

    public boolean tokenExists(String token) {
        return passwordResetTokenRepository.existsByToken(token);
    }

    public User loadUserByToken(String token) {
        User user = null;
        PasswordResetToken passwordResetToken = passwordResetTokenRepository.findByToken(token);
        if (null != passwordResetToken) {
            user = passwordResetToken.getUser();
        }
        return user;
    }

    public PasswordResetToken save(PasswordResetToken token) {
        return passwordResetTokenRepository.save(token);
    }

    public void deleteByUser(User user) {
        passwordResetTokenRepository.deleteByUser(user);
    }

}
