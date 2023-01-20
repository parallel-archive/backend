package hu.codeandsoda.osa.registration.service;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import hu.codeandsoda.osa.account.user.domain.User;
import hu.codeandsoda.osa.registration.domain.RegistrationToken;
import hu.codeandsoda.osa.registration.repository.RegistrationTokenRepository;

@Service
public class RegistrationTokenService {

    private static Logger logger = LoggerFactory.getLogger(RegistrationTokenService.class);

    @Autowired
    private RegistrationTokenRepository registrationTokenRepository;

    public String createRegistrationToken(User user) {
        String token = UUID.randomUUID().toString();
        RegistrationToken registrationToken = new RegistrationToken(token, user);
        
        String logMessage = new StringBuilder().append("action=generateRegistrationToken, status=success, userId: ").append(user.getId()).toString();
        logger.info(logMessage);
        return registrationTokenRepository.save(registrationToken).getToken();
    }

    public boolean tokenExists(String token) {
        return registrationTokenRepository.existsByToken(token);
    }

    public User loadUserByToken(String token) {
        User user = null;
        RegistrationToken registrationToken = registrationTokenRepository.findByToken(token);
        if (null != registrationToken) {
            user = registrationToken.getUser();
        }
        return user;
    }

    public void deleteByUser(User user) {
        registrationTokenRepository.deleteByUser(user);
    }

}
