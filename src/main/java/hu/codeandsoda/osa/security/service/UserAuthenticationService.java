package hu.codeandsoda.osa.security.service;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import hu.codeandsoda.osa.account.user.data.UserData;
import hu.codeandsoda.osa.security.data.AuthenticationSuccessData;

@Service
public class UserAuthenticationService {

    public AuthenticationSuccessData loadUserData(Authentication authentication) {
        AuthenticationSuccessData successData = new AuthenticationSuccessData.AuthenticationSuccesDataBuilder().setDefaultPath("/default").build();
        successData.setUser(collectUserDetails(authentication));
        return successData;
    }

    private UserData collectUserDetails(Authentication authentication) {
        String userName = authentication.getName();
        UserData user = new UserData.UserDataBuilder().setName(userName).build();
        return user;
    }

}
