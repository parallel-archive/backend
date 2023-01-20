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
import hu.codeandsoda.osa.resetpassword.domain.PasswordResetToken;
import hu.codeandsoda.osa.resetpassword.repository.PasswordResetTokenRepository;
import hu.codeandsoda.osa.resetpassword.service.PasswordResetTokenService;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@EnabledIf(value = "#{'${spring.profiles.active}' == 'unittest'}", loadContext = true)
class PasswordResetTokenServiceTest {

    @Autowired
    private PasswordResetTokenService passwordResetTokenService;

    @MockBean
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Test
    @DisplayName("Test create user's first password reset token is success")
    void givenUsersFirstPasswordResetTokenIsCreatedWhenCreatePasswordResetTokenThenReturnsToken() {
        User user = MockDataFactory.getEnabledMockUser();
        String expected = "password reset token";
        PasswordResetToken savedToken = MockDataFactory.getPasswordResetTokenWithId(user, expected);

        when(passwordResetTokenRepository.findByUser(Mockito.any())).thenReturn(null);
        when(passwordResetTokenRepository.save(Mockito.any())).thenReturn(savedToken);


        String result = passwordResetTokenService.createPasswordResetToken(user);
        assertEquals(expected, result);
    }

    @Test
    @DisplayName("Test create new password reset token for user is success")
    void givenNewPasswordResetTokenIsCreatedWhenCreatePasswordResetTokenThenReturnsToken() {
        User user = MockDataFactory.getEnabledMockUser();
        PasswordResetToken oldToken = MockDataFactory.getPasswordResetTokenWithId(user, "old token");
        String expected = "password reset token";
        PasswordResetToken newToken = MockDataFactory.getPasswordResetTokenWithId(user, expected);

        when(passwordResetTokenRepository.findByUser(Mockito.any())).thenReturn(oldToken);
        when(passwordResetTokenRepository.save(Mockito.any())).thenReturn(newToken);

        String result = passwordResetTokenService.createPasswordResetToken(user);
        assertEquals(expected, result);
    }

    @Test
    @DisplayName("Test user token exists")
    void givenTokenExistsWhenTokenExistsThenReturnsTrue() {
        String token = "token";
        when(passwordResetTokenRepository.existsByToken(token)).thenReturn(true);
        assertTrue(passwordResetTokenService.tokenExists(token));
    }

    @Test
    @DisplayName("Test user token does not exist")
    void givenTokenDoesNotExistWhenTokenExistsThenReturnsFalse() {
        String token = "token";
        when(passwordResetTokenRepository.existsByToken(token)).thenReturn(true);
        assertFalse(passwordResetTokenService.tokenExists("invalid token"));
    }

    @Test
    @DisplayName("Test load user by token returns user")
    void givenTokenExistsWhenLoadUserByTokenThenReturnsUser() {
        User expected = MockDataFactory.getEnabledMockUser();
        String token = "token";
        PasswordResetToken passwordResetToken = MockDataFactory.getPasswordResetTokenWithId(expected, token);
        
        when(passwordResetTokenRepository.findByToken(token)).thenReturn(passwordResetToken);
        
        User result = passwordResetTokenService.loadUserByToken(token);
        assertEquals(expected, result);
    }

    @Test
    @DisplayName("Test load user by token fails with invalid token")
    void givenTokenDoesNotExistWhenLoadUserByTokenThenReturnsNull() {
        User expected = MockDataFactory.getEnabledMockUser();
        String token = "token";
        PasswordResetToken passwordResetToken = MockDataFactory.getPasswordResetTokenWithId(expected, token);

        when(passwordResetTokenRepository.findByToken(token)).thenReturn(passwordResetToken);

        assertNull(passwordResetTokenService.loadUserByToken("invalid token"));
    }

    @Test
    @DisplayName("Test password reset token save")
    void givenPasswordResetTokenIsSavedWhenSaveThenReturnsToken() {
        User user = MockDataFactory.getMockUserWithId();
        PasswordResetToken tokenToSave = MockDataFactory.getPasswordResetTokenWithoutId(user, "token");
        PasswordResetToken expected = MockDataFactory.getPasswordResetTokenWithId(user, "token");
        when(passwordResetTokenRepository.save(tokenToSave)).thenReturn(expected);

        PasswordResetToken result = passwordResetTokenService.save(tokenToSave);
        assertEquals(expected, result);
    }

}
