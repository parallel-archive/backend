package hu.codeandsoda.osa.account.service;

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
import hu.codeandsoda.osa.changepassword.data.ChangePasswordData;
import hu.codeandsoda.osa.changepassword.service.ChangePasswordService;
import hu.codeandsoda.osa.security.service.UserService;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@EnabledIf(value = "#{'${spring.profiles.active}' == 'unittest'}", loadContext = true)
public class ChangePasswordServiceTest {

    @Autowired
    private ChangePasswordService changePasswordService;

    @MockBean
    private UserService userService;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("Test change password")
    void givenChangePasswordIsSuccessWhenChangePasswordThenPasswordIsChanged() {
        User user = MockDataFactory.getEnabledMockUser();
        ChangePasswordData changePasswordData = MockDataFactory.getChangePasswordData();

        when(passwordEncoder.encode(Mockito.anyString())).thenReturn("encoded password");
        when(userService.save(Mockito.any())).thenReturn(user);

        changePasswordService.changePassword(changePasswordData, user);
        verify(passwordEncoder, times(1)).encode(Mockito.anyString());
        verify(userService, times(1)).save(Mockito.any());
    }

}
