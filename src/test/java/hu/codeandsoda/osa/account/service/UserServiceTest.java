package hu.codeandsoda.osa.account.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.junit.jupiter.EnabledIf;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import hu.codeandsoda.osa.MockDataFactory;
import hu.codeandsoda.osa.account.user.domain.User;
import hu.codeandsoda.osa.account.user.repository.UserRepository;
import hu.codeandsoda.osa.security.service.UserService;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@EnabledIf(value = "#{'${spring.profiles.active}' == 'unittest'}", loadContext = true)
class UserServiceTest {

    @Autowired
    private UserService userService;

    @MockBean
    private UserRepository userRepository;


    @Test
    @DisplayName("Test loading User by email returns User")
    void givenUserExistsWhenLoadingUserByUserNameThenReturnsUser() {
        final String email = "test@test.com";
        User expected = MockDataFactory.getMockUserWithId();
        when(userRepository.findByEmail(email)).thenReturn(expected);

        UserDetails result = userService.loadUserByUsername(email);
        assertEquals(expected, result);
    }

    @Test
    @DisplayName("Test loading User by email throws UsernameNotFoundException")
    void givenUserDoesNotExistWhenLoadingUserByUserNameThenThrowsUsernameNotFoundException() {
        final String email = "test@test.com";
        when(userRepository.findByEmail(email)).thenReturn(null);
        assertThrows(UsernameNotFoundException.class, () -> userService.loadUserByUsername(email));
    }

    @Test
    @DisplayName("Test checking if user exists returns true")
    void givenUserExistsWhenCheckingUserExistsThenReturnsTrue() {
        final String email = "test@test.com";
        User expected = MockDataFactory.getMockUserWithId();
        when(userRepository.findByEmail(email)).thenReturn(expected);

        assertTrue(userService.userExists(email));
    }

    @Test
    @DisplayName("Test checking if user exists returns false")
    void givenUserExistsWhenCheckingUserExistsThenReturnsFalse() {
        final String email = "test@test.com";
        when(userRepository.findByEmail(email)).thenReturn(null);

        assertFalse(userService.userExists(email));
    }

    @Test
    @DisplayName("Test find user by email returns user")
    void givenUserExistsWhenFindByEmailThenReturnsUser() {
        final String email = "test@test.com";
        User expected = MockDataFactory.getMockUserWithId();
        when(userRepository.findByEmail(email)).thenReturn(expected);

        User result = userService.findByEmail(email);
        assertEquals(expected, result);
    }

    @Test
    @DisplayName("Test find user by email returns null")
    void givenUserDoesNotExistWhenFindByEmailThenReturnsNull() {
        final String email = "test@test.com";
        when(userRepository.findByEmail(email)).thenReturn(null);
        assertNull(userService.findByEmail(email));
    }

    @Test
    @DisplayName("Test save user returns user")
    void givenUserIsSavedWhenSavingUserThenReturnsUser() {
        User expected = MockDataFactory.getMockUserWithId();
        when(userRepository.save(Mockito.any())).thenReturn(expected);
        
        User result = userService.save(expected);
        assertEquals(expected, result);
    }

    @Test
    @DisplayName("Test enable user returns user with enabled field set to true")
    void givenEnableUserIsSuccessWhenEnableUserThenReturnsUser() {
        User expected = MockDataFactory.getEnabledMockUser();
        when(userRepository.save(Mockito.any())).thenReturn(expected);
        
        User user = MockDataFactory.getMockUserWithId();
        User result = userService.enableUser(user);
        
        assertEquals(expected, result);
    }

}
