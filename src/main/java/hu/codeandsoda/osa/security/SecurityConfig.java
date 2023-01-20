package hu.codeandsoda.osa.security;

import java.io.IOException;
import java.util.LinkedHashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.DelegatingAuthenticationEntryPoint;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.StringUtils;

import hu.codeandsoda.osa.account.user.web.UserController;
import hu.codeandsoda.osa.documentpublish.web.PublishedDocumentController;
import hu.codeandsoda.osa.ipfs.web.IpfsController;
import hu.codeandsoda.osa.login.web.LoginController;
import hu.codeandsoda.osa.registration.web.RegistrationController;
import hu.codeandsoda.osa.resetpassword.web.ForgottenPasswordController;
import hu.codeandsoda.osa.search.web.SearchController;
import hu.codeandsoda.osa.security.service.UserService;


@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    
    private static final String LOGOUT_URL = "/api/logout";
    private static final String TARGET_URL_PARAMETER_NAME = "targetUrl";
    private static final String SPRING_SECURITY_SAVED_REQUEST_NAME = "SPRING_SECURITY_SAVED_REQUEST";

    @Autowired
    private CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
    
    @Autowired
    private CustomRedirectStrategy customRedirectStrategy;

    @Autowired
    private UserService userDetailsService;

    @Override
    protected void configure(final AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(authenticationProvider());
    }

    @Bean
    public DelegatingAuthenticationEntryPoint noAuthorizedEntryPoint() {
        LinkedHashMap<RequestMatcher, AuthenticationEntryPoint> entryPoints = new LinkedHashMap<>();

        entryPoints.put(new AntPathRequestMatcher("/api/**"), customAuthenticationEntryPoint);
        entryPoints.put(new AntPathRequestMatcher("/**"), loginUrlAuthenticationEntryPoint());

        DelegatingAuthenticationEntryPoint noAuthorizedEntryPoint = new DelegatingAuthenticationEntryPoint(entryPoints);
        noAuthorizedEntryPoint.setDefaultEntryPoint(customAuthenticationEntryPoint);
        return noAuthorizedEntryPoint;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.csrf().disable();
        http.headers().frameOptions().sameOrigin();

        http.authorizeRequests().requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll();

        http.authorizeRequests().antMatchers(HttpMethod.GET, "/h2-console/*").permitAll();
        http.authorizeRequests().antMatchers(HttpMethod.POST, "/h2-console/*").permitAll();
        
        http.authorizeRequests().antMatchers(HttpMethod.GET, LoginController.LOGIN_URL).permitAll();
        http.authorizeRequests().antMatchers(HttpMethod.POST, LOGOUT_URL).permitAll();

        http.authorizeRequests().antMatchers(HttpMethod.GET, RegistrationController.REGISTRATION_URL).permitAll();
        http.authorizeRequests().antMatchers(HttpMethod.POST, RegistrationController.REGISTRATION_URL).permitAll();

        http.authorizeRequests().antMatchers(HttpMethod.GET, RegistrationController.CONFIRM_REGISTRATION_URL).permitAll();

        http.authorizeRequests().antMatchers(HttpMethod.GET, ForgottenPasswordController.FORGOT_PASSWORD_URL).permitAll();
        http.authorizeRequests().antMatchers(HttpMethod.POST, ForgottenPasswordController.FORGOT_PASSWORD_URL).permitAll();

        http.authorizeRequests().antMatchers(HttpMethod.GET, ForgottenPasswordController.RESET_PASSWORD_FORM_URL).permitAll();
        http.authorizeRequests().antMatchers(HttpMethod.POST, ForgottenPasswordController.RESET_PASSWORD_SUBMIT_URL).permitAll();

        http.authorizeRequests().antMatchers(HttpMethod.GET, PublishedDocumentController.PUBLISHED_DOCUMENTS_URL).permitAll();
        http.authorizeRequests().antMatchers(HttpMethod.GET, PublishedDocumentController.PUBLISHED_DOCUMENT_URL).permitAll();

        http.authorizeRequests().antMatchers(HttpMethod.GET, IpfsController.IPFS_HELP_PAGE_URL).permitAll();

        http.authorizeRequests().antMatchers(HttpMethod.GET, SearchController.TAG_AUTOSUGGEST_URL).permitAll();

        http.authorizeRequests().antMatchers(HttpMethod.GET, UserController.LOAD_USER_MENU_DATA).permitAll();

        http.authorizeRequests().antMatchers("/actuator/**").permitAll();

        http.authorizeRequests().antMatchers(HttpMethod.GET, "/*").authenticated().and().exceptionHandling().authenticationEntryPoint(noAuthorizedEntryPoint());
         
        http.authorizeRequests().anyRequest().fullyAuthenticated().and().formLogin().loginPage(LoginController.LOGIN_URL).permitAll().successHandler(loginSuccessHandler());
        
        http.logout().logoutUrl(LOGOUT_URL).logoutSuccessUrl(PublishedDocumentController.PUBLISHED_DOCUMENTS_URL);
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        final DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SavedRequestAwareAuthenticationSuccessHandler loginSuccessHandler() {
        SavedRequestAwareAuthenticationSuccessHandler successHandler = new SavedRequestAwareAuthenticationSuccessHandler() {
            @Override
            public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
                SavedRequest savedRequest = (SavedRequest) request.getSession().getAttribute(SPRING_SECURITY_SAVED_REQUEST_NAME);
                if (null != savedRequest && StringUtils.hasText(savedRequest.getRedirectUrl()) && savedRequest.getRedirectUrl().contains("/api")) {
                    request.getSession().removeAttribute(SPRING_SECURITY_SAVED_REQUEST_NAME);
                }

                super.onAuthenticationSuccess(request, response, authentication);
            }
        };
        successHandler.setRedirectStrategy(customRedirectStrategy);
        successHandler.setDefaultTargetUrl(PublishedDocumentController.PUBLISHED_DOCUMENTS_URL);
        successHandler.setTargetUrlParameter(TARGET_URL_PARAMETER_NAME);
        return successHandler;
    }

    @Bean
    public LoginUrlAuthenticationEntryPoint loginUrlAuthenticationEntryPoint() {
        return new LoginUrlAuthenticationEntryPoint(LoginController.LOGIN_URL);
    }
}
