package hu.codeandsoda.osa.registration.web;

import java.text.MessageFormat;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import hu.codeandsoda.osa.account.user.data.UserData;
import hu.codeandsoda.osa.prometheus.service.PrometheusReporterService;
import hu.codeandsoda.osa.registration.data.RegistrationData;
import hu.codeandsoda.osa.registration.data.RegistrationTokenData;
import hu.codeandsoda.osa.registration.service.RegistrationService;
import hu.codeandsoda.osa.registration.validation.RegistrationDataValidator;
import hu.codeandsoda.osa.registration.validation.RegistrationTokenValidator;
import hu.codeandsoda.osa.security.data.AuthenticationSuccessData;
import hu.codeandsoda.osa.util.ErrorCode;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;

@Controller
public class RegistrationController {

    public static final String REGISTRATION_URL = "/register";
    public static final String CONFIRM_REGISTRATION_URL = "/confirm-register/{token}";

    private static final String REGISTRATION_TEMPLATE = "register";
    private static final String CONFIRM_REGISTRATION_TEMPLATE = "confirm_register";

    private static final String LOGIN_FORM_NOTES = "Returned model attributes: RegistrationData as registrationData. See attributes under 4. Definitions.";
    private static final String LOGIN_SUBMIT_NOTES = "Returned model attributes: AuthenticationSuccessData as success. See attributes under 4. Definitions.";
    private static final String CONFIRM_REGISTRATION_NOTES = "Returned model attributes: AuthenticationSuccessData as feedback. See attributes under 4. Definitions.";

    private static Logger logger = LoggerFactory.getLogger(RegistrationController.class);

    @Autowired
    private RegistrationService registrationService;

    @Autowired
    private RegistrationDataValidator registrationDataValidator;

    @Autowired
    private RegistrationTokenValidator registrationTokenValidator;

    @Autowired
    private PrometheusReporterService prometheusReporterService;

    @ApiOperation(value = "Load registration form", notes = LOGIN_FORM_NOTES)
    @ApiResponse(code = 200, message = LOGIN_FORM_NOTES, response = RegistrationData.class)
    @GetMapping(REGISTRATION_URL)
    public ModelAndView registerForm() {
        ModelAndView mav = new ModelAndView(REGISTRATION_TEMPLATE);
        mav.addObject("registrationData", new RegistrationData());
        return mav;
    }

    @ApiOperation(value = "Registration", notes = LOGIN_SUBMIT_NOTES)
    @ApiResponse(code = 200, message = LOGIN_SUBMIT_NOTES, response = AuthenticationSuccessData.class)
    @PostMapping(REGISTRATION_URL)
    public ModelAndView registerUser(@Valid @ModelAttribute RegistrationData registrationData, Errors errors) {
        ModelAndView mav = new ModelAndView(REGISTRATION_TEMPLATE);
        if (!errors.hasErrors()) {
            try {
                UserData user = registrationService.registerUser(registrationData, errors);
                registrationService.sendConfirmRegistrationEmail(user);

                AuthenticationSuccessData successData = createSuccessData(user);
                mav.addObject("success", successData);
            } catch (Exception e) {
                String errorCode = ErrorCode.REGISTRATION_ERROR.toString();
                errors.reject(errorCode, "Error during registration.");
                String logMessage = MessageFormat.format("action=registration, status=error, email={0}, errorCode={1}, error={2}", registrationData.getEmail(), errorCode,
                        e.getMessage());
                logger.error(logMessage, e);
            }
        }

        return mav;
    }

    @ApiOperation(value = "Confirm registration", notes = CONFIRM_REGISTRATION_NOTES)
    @ApiResponse(code = 200, message = CONFIRM_REGISTRATION_NOTES, response = AuthenticationSuccessData.class)
    @GetMapping(CONFIRM_REGISTRATION_URL)
    public ModelAndView confirmRegistration(@PathVariable String token) {
        RegistrationTokenData registrationToken = new RegistrationTokenData.RegistrationTokenDataBuilder().setToken(token).build();
        Errors errors = new BeanPropertyBindingResult(registrationToken, "registrationToken");
        registrationTokenValidator.validate(registrationToken, errors);

        AuthenticationSuccessData successData = new AuthenticationSuccessData();
        if (!errors.hasErrors()) {
            try {
                UserData user = registrationService.enableUser(registrationToken);
                successData = createSuccessData(user);
            } catch (Exception e) {
                String errorCode = ErrorCode.CONFIRM_REGISTRATION_ERROR.toString();
                errors.reject(errorCode, "Error during registration confirmation.");
                String logMessage = MessageFormat.format("action=registrationValidation, status=error, errorCode={0}", errorCode);
                logger.error(logMessage, e);

                prometheusReporterService.sendConfirmRegistrationError(e.getMessage());
            }
        }

        ModelAndView mav = new ModelAndView(CONFIRM_REGISTRATION_TEMPLATE);
        mav.addObject("feedback", successData);
        return mav;
    }

    private AuthenticationSuccessData createSuccessData(UserData user) {
        return new AuthenticationSuccessData.AuthenticationSuccesDataBuilder().setUser(user).setDefaultPath("/default").build();
    }

    @InitBinder("registrationData")
    public void setupRegistrationDataDataBinder(WebDataBinder binder) {
        binder.addValidators(registrationDataValidator);
    }

}
