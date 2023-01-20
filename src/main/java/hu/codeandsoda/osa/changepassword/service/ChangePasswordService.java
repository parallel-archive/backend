package hu.codeandsoda.osa.changepassword.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import hu.codeandsoda.osa.account.user.domain.User;
import hu.codeandsoda.osa.changepassword.data.ChangePasswordData;
import hu.codeandsoda.osa.security.service.UserService;

@Service
public class ChangePasswordService {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public void changePassword(ChangePasswordData changePasswordData, User user) {
        final String newPassword = changePasswordData.getNewPassword();
        user.setPassword(passwordEncoder.encode(newPassword));
        userService.save(user);
    }

}
