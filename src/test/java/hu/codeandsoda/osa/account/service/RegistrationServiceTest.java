package hu.codeandsoda.osa.account.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.EnabledIf;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.validation.Errors;

import hu.codeandsoda.osa.MockDataFactory;
import hu.codeandsoda.osa.account.user.data.UserData;
import hu.codeandsoda.osa.account.user.domain.User;
import hu.codeandsoda.osa.jms.service.email.EmailMessageService;
import hu.codeandsoda.osa.registration.data.RegistrationData;
import hu.codeandsoda.osa.registration.data.RegistrationTokenData;
import hu.codeandsoda.osa.registration.exception.RegistrationException;
import hu.codeandsoda.osa.registration.service.RegistrationService;
import hu.codeandsoda.osa.registration.service.RegistrationTokenService;
import hu.codeandsoda.osa.security.service.UserService;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@EnabledIf(value = "#{'${spring.profiles.active}' == 'unittest'}", loadContext = true)
class RegistrationServiceTest {

    @Autowired
    private RegistrationService registrationService;

    @MockBean
    private UserService userService;

    @MockBean
    private RegistrationTokenService registrationTokenService;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @MockBean
    private EmailMessageService emailMessageService;

    @Test
    @DisplayName("Test user registration is success")
    void givenUserRegistrationIsSuccessWhenRegisterUserThenReturnsUserData() throws RegistrationException {
        User user = MockDataFactory.getMockUserWithId();
        UserData expected = MockDataFactory.getMockUserData();

        when(passwordEncoder.encode(Mockito.anyString())).thenReturn("encoded token");
        when(userService.save(Mockito.any())).thenReturn(user);

        RegistrationData registrationData = MockDataFactory.getRegistrationData();
        Errors errors = MockDataFactory.getRegistrationDataErrors();
        UserData result = registrationService.registerUser(registrationData, errors);
        assertEquals(expected, result);
    }

    @Test
    @DisplayName("Test user registration failed returns with empty UserData")
    void givenUserRegistrationFailedWhenRegisterUserThenReturnsEmptyUserData() throws RegistrationException {
        UserData expected = new UserData();
        when(passwordEncoder.encode(Mockito.anyString())).thenReturn("encoded token");
        when(userService.save(Mockito.any())).thenReturn(null);

        RegistrationData registrationData = MockDataFactory.getRegistrationData();
        Errors errors = MockDataFactory.getRegistrationDataErrors();
        UserData result = registrationService.registerUser(registrationData, errors);
        assertEquals(expected, result);
    }

    @Test
    @DisplayName("Test confirm registration e-mail sending")
    void givenEmailSendingIsSuccessWhenSendConfirmRegistrationEmailThenEmailIsSent() {
        User user = MockDataFactory.getMockUserWithId();
        
        when(userService.findByEmail(Mockito.anyString())).thenReturn(user);
        when(registrationTokenService.createRegistrationToken(user)).thenReturn("token");
        doNothing().when(emailMessageService).send(Mockito.any());

        UserData userData = MockDataFactory.getMockUserData();
        registrationService.sendConfirmRegistrationEmail(userData);

        verify(userService, times(1)).findByEmail(Mockito.anyString());
        verify(registrationTokenService, times(1)).createRegistrationToken(user);
        verify(emailMessageService, times(1)).send(Mockito.any());
    }

    @Test
    @DisplayName("Test enable user is success")
    void givenEnableUserIsSuccessWhenEnableUserThenReturnsUserData() {
        User user = MockDataFactory.getMockUserWithId();
        UserData expected = MockDataFactory.getMockUserData();
        
        when(registrationTokenService.loadUserByToken(Mockito.anyString())).thenReturn(user);
        when(userService.enableUser(Mockito.any())).thenReturn(user);

        RegistrationTokenData tokenData = MockDataFactory.getRegistrationTokenData();
        UserData result = registrationService.enableUser(tokenData);

        assertEquals(expected, result);
    }

}
