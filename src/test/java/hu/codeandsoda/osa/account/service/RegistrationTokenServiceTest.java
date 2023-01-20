package hu.codeandsoda.osa.account.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.EnabledIf;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import hu.codeandsoda.osa.MockDataFactory;
import hu.codeandsoda.osa.account.user.domain.User;
import hu.codeandsoda.osa.registration.domain.RegistrationToken;
import hu.codeandsoda.osa.registration.repository.RegistrationTokenRepository;
import hu.codeandsoda.osa.registration.service.RegistrationTokenService;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@EnabledIf(value = "#{'${spring.profiles.active}' == 'unittest'}", loadContext = true)
class RegistrationTokenServiceTest {

    @Autowired
    private RegistrationTokenService registrationTokenService;

    @MockBean
    private RegistrationTokenRepository registrationTokenRepository;

    @Test
    @DisplayName("Test registration token creation is success")
    void givenTokenIsCreatedWhenCreateRegistrationTokenThenReturnsToken() {
        User user = MockDataFactory.getMockUserWithoutId();
        RegistrationToken token = MockDataFactory.getRegistrationTokenWithId(user);
        when(registrationTokenRepository.save(Mockito.any())).thenReturn(token);
        
        String expected = "token";
        String result = registrationTokenService.createRegistrationToken(user);
        assertEquals(expected, result);
    }

    @Test
    @DisplayName("Test registration token exists")
    void givenTokenExistsWhenExistsByTokenThenReturnsTrue() {
        String token = "token";
        when(registrationTokenRepository.existsByToken(token)).thenReturn(true);
        assertTrue(registrationTokenService.tokenExists(token));
    }

    @Test
    @DisplayName("Test registration token does not exist")
    void givenTokenDoesNotExistWhenExistsByTokenThenReturnsFalse() {
        when(registrationTokenRepository.existsByToken("valid token")).thenReturn(true);
        assertFalse(registrationTokenService.tokenExists("token"));
    }

    @Test
    @DisplayName("Test load User by token returns User")
    void givenTokenIsValidWhenFindUserByTokenThenReturnsUser() {
        User expected = MockDataFactory.getMockUserWithId();
        String token = "token";
        RegistrationToken registrationToken = MockDataFactory.getRegistrationTokenWithId(expected);
        when(registrationTokenRepository.findByToken(token)).thenReturn(registrationToken);

        User result = registrationTokenService.loadUserByToken(token);
        assertEquals(expected, result);
    }

    @Test
    @DisplayName("Test load User by invalid token returns null")
    void givenTokenIsInvalidWhenFindUserByTokenThenReturnsNull() {
        User user = MockDataFactory.getMockUserWithId();
        String token = "token";
        RegistrationToken registrationToken = MockDataFactory.getRegistrationTokenWithId(user);
        when(registrationTokenRepository.findByToken(token)).thenReturn(registrationToken);

        assertNull(registrationTokenService.loadUserByToken("invalid token"));
    }

}
