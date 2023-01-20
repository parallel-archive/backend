package hu.codeandsoda.osa.account.service;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
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

import hu.codeandsoda.osa.MockDataFactory;
import hu.codeandsoda.osa.account.user.domain.User;
import hu.codeandsoda.osa.jms.service.email.EmailMessageService;
import hu.codeandsoda.osa.resetpassword.data.RecoveryEmailData;
import hu.codeandsoda.osa.resetpassword.data.ResetPasswordData;
import hu.codeandsoda.osa.resetpassword.service.ForgottenPasswordService;
import hu.codeandsoda.osa.resetpassword.service.PasswordResetTokenService;
import hu.codeandsoda.osa.security.service.UserService;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@EnabledIf(value = "#{'${spring.profiles.active}' == 'unittest'}", loadContext = true)
class ForgottenPasswordServiceTest {

    @Autowired
    private ForgottenPasswordService forgottenPasswordService;

    @MockBean
    private UserService userService;

    @MockBean
    private PasswordResetTokenService passwordResetTokenService;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @MockBean
    private EmailMessageService emailMessageService;

    @Test
    @DisplayName("Recover password when user exists")
    void givenUserExistsWhenRecoverPasswordThenCreatesPasswordResetTokenAndSendsEmail() {
        User user = MockDataFactory.getEnabledMockUser();

        when(userService.findByEmail(Mockito.anyString())).thenReturn(user);
        when(passwordResetTokenService.createPasswordResetToken(user)).thenReturn("token");
        doNothing().when(emailMessageService).send(Mockito.any());

        RecoveryEmailData recoveryEmailData = MockDataFactory.getRecoveryEmailData();
        forgottenPasswordService.recoverPassword(recoveryEmailData);
        verify(userService, times(1)).findByEmail(Mockito.anyString());
        verify(passwordResetTokenService, times(1)).createPasswordResetToken(Mockito.any());
        verify(emailMessageService, times(1)).send(Mockito.any());
    }

    @Test
    @DisplayName("Recover password when user does not exist")
    void givenUserDoesNotExistWhenRecoverPasswordThenDoesNotRecoverPassword() {
        when(userService.findByEmail(Mockito.anyString())).thenReturn(null);

        RecoveryEmailData recoveryEmailData = MockDataFactory.getRecoveryEmailData();
        forgottenPasswordService.recoverPassword(recoveryEmailData);
        verify(userService, times(1)).findByEmail(Mockito.anyString());
        verify(passwordResetTokenService, never()).createPasswordResetToken(Mockito.any());
        verify(emailMessageService, never()).send(Mockito.any());
    }

    @Test
    @DisplayName("Test reset password success")
    void givenPasswordUpdateIsSuccessWhenResetPasswordThenPasswordIsUpdatedAndSendsEmail() {
        String token = "reset token";
        User user = MockDataFactory.getEnabledMockUser();
        
        String newPassword = "newPassword";
        User savedUser = MockDataFactory.getEnabledMockUser();
        savedUser.setPassword(newPassword);

        ResetPasswordData resetPasswordData = MockDataFactory.getResetPasswordData(token);

        when(passwordResetTokenService.loadUserByToken(token)).thenReturn(user);
        when(passwordEncoder.encode(resetPasswordData.getPassword())).thenReturn(newPassword);
        when(userService.save(user)).thenReturn(savedUser);
        doNothing().when(emailMessageService).send(Mockito.any());

        forgottenPasswordService.resetPassword(resetPasswordData);

        verify(passwordResetTokenService, times(1)).loadUserByToken(Mockito.anyString());
        verify(passwordEncoder, times(1)).encode(Mockito.anyString());
        verify(userService, times(1)).save(user);
        verify(emailMessageService, times(1)).send(Mockito.any());
    }

    @Test
    @DisplayName("Test reset password error")
    void givenPasswordUpdateIsNotSavedWhenResetPasswordThenPasswordIsNotReset() {
        String token = "reset token";
        User user = MockDataFactory.getEnabledMockUser();

        ResetPasswordData resetPasswordData = MockDataFactory.getResetPasswordData(token);

        when(passwordResetTokenService.loadUserByToken(token)).thenReturn(user);
        when(passwordEncoder.encode(resetPasswordData.getPassword())).thenReturn("newPassword");
        when(userService.save(user)).thenReturn(null);

        forgottenPasswordService.resetPassword(resetPasswordData);

        verify(passwordResetTokenService, times(1)).loadUserByToken(Mockito.anyString());
        verify(passwordEncoder, times(1)).encode(Mockito.anyString());
        verify(userService, times(1)).save(user);
        verify(emailMessageService, never()).send(Mockito.any());
    }

}
