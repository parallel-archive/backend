package hu.codeandsoda.osa.resetpassword.web;

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

import hu.codeandsoda.osa.general.data.GenericResponse;
import hu.codeandsoda.osa.general.data.ResultCode;
import hu.codeandsoda.osa.resetpassword.data.RecoveryEmailData;
import hu.codeandsoda.osa.resetpassword.data.ResetPasswordData;
import hu.codeandsoda.osa.resetpassword.service.ForgottenPasswordService;
import hu.codeandsoda.osa.resetpassword.validation.RecoveryEmailDataValidator;
import hu.codeandsoda.osa.resetpassword.validation.ResetPasswordDataValidator;
import hu.codeandsoda.osa.util.ErrorCode;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;

@Controller
public class ForgottenPasswordController {

    public static final String FORGOT_PASSWORD_URL = "/forgotpassword";
    public static final String RESET_PASSWORD_FORM_URL = "/resetpassword/{token}";
    public static final String RESET_PASSWORD_SUBMIT_URL = "/resetpassword";

    private static final String FORGOT_PASSWORD_TEMPLATE = "forgotpassword";
    private static final String RESET_PASSWORD_TEMPLATE = "reset_password";

    private static final String FORGOT_PASSWORD_FORM_NOTES = "Returned model attributes: RecoveryEmailData as RecoveryEmailData. See attributes under 4. Definitions.";
    private static final String FORGOT_PASSWORD_SUBMIT_NOTES = "Returned model attributes: GenericResponse as success. See attributes under 4. Definitions.";
    private static final String RESET_PASSWORD_FORM_NOTES = "Returned model attributes: ResetPasswordData as resetPasswordData. See attributes under 4. Definitions.";
    private static final String RESET_PASSWORD_SUBMIT_NOTES = "Returned model attributes: GenericResponse as success. See attributes under 4. Definitions.";

    private static Logger logger = LoggerFactory.getLogger(ForgottenPasswordController.class);

    @Autowired
    private ForgottenPasswordService forgottenPasswordService;

    @Autowired
    private RecoveryEmailDataValidator recoveryEmailDataValidator;

    @Autowired
    private ResetPasswordDataValidator resetPasswordDataValidator;

    @Autowired
    private ResetPasswordTokenValidator resetPasswordTokenValidator;

    @ApiOperation(value = "Load forgot password form", notes = FORGOT_PASSWORD_FORM_NOTES)
    @ApiResponse(code = 200, message = FORGOT_PASSWORD_FORM_NOTES, response = RecoveryEmailData.class)
    @GetMapping(FORGOT_PASSWORD_URL)
    public ModelAndView forgotPasswordForm() {
        ModelAndView mav = new ModelAndView(FORGOT_PASSWORD_TEMPLATE);
        mav.addObject("recoveryEmailData", new RecoveryEmailData());
        return mav;
    }

    @ApiOperation(value = "Submit forgot password request", notes = FORGOT_PASSWORD_SUBMIT_NOTES)
    @ApiResponse(code = 200, message = FORGOT_PASSWORD_SUBMIT_NOTES, response = GenericResponse.class)
    @PostMapping(FORGOT_PASSWORD_URL)
    public ModelAndView forgotPasswordSubmit(@Valid @ModelAttribute RecoveryEmailData recoveryEmailData, Errors errors) {
        ModelAndView mav = new ModelAndView(FORGOT_PASSWORD_TEMPLATE);
        if (!errors.hasErrors()) {
            try {
                forgottenPasswordService.recoverPassword(recoveryEmailData);
            } catch (Exception e) {
                String errorCode = ErrorCode.SUBMIT_FORGOTTEN_PASSWORD_REQUEST_ERROR.toString();
                errors.reject(errorCode, "Could not submit forgotten password request.");
                String logMessage = MessageFormat.format("action=passwordRecovery, status=error, email={0}, errorCode={1}, error={2}", recoveryEmailData.getEmail(), errorCode,
                        e.getMessage());
                logger.error(logMessage, e);
            }
        }
        GenericResponse response = new GenericResponse.GenericResponseBuilder().setResultCode(ResultCode.SUCCESS).build();
        mav.addObject("feedback", response);
        return mav;
    }

    @ApiOperation(value = "Load reset password form", notes = RESET_PASSWORD_FORM_NOTES)
    @ApiResponse(code = 200, message = RESET_PASSWORD_FORM_NOTES, response = ResetPasswordData.class)
    @GetMapping(RESET_PASSWORD_FORM_URL)
    public ModelAndView resetPasswordForm(@PathVariable String token) {
        ResetPasswordData resetPasswordData = new ResetPasswordData.ResetPasswordDataBuilder().setToken(token).build();
        Errors errors = new BeanPropertyBindingResult(resetPasswordData, "resetPasswordData");
        resetPasswordTokenValidator.validate(resetPasswordData.getToken(), errors);

        ModelAndView mav = new ModelAndView();
        if (!errors.hasErrors()) {
            mav.setViewName(RESET_PASSWORD_TEMPLATE);
            mav.addObject("resetPasswordData", resetPasswordData);
        } else {
            mav.setViewName("redirect:/login");
        }

        return mav;
    }

    @ApiOperation(value = "Submit reset password request", notes = RESET_PASSWORD_SUBMIT_NOTES)
    @ApiResponse(code = 200, message = RESET_PASSWORD_SUBMIT_NOTES, response = GenericResponse.class)
    @PostMapping(RESET_PASSWORD_SUBMIT_URL)
    public ModelAndView resetPasswordSubmit(@Valid @ModelAttribute ResetPasswordData resetPasswordData, Errors errors) {
        ModelAndView mav = new ModelAndView(RESET_PASSWORD_TEMPLATE);
        if (!errors.hasErrors()) {
            try {
                forgottenPasswordService.resetPassword(resetPasswordData);
                GenericResponse successResult = new GenericResponse.GenericResponseBuilder().setResultCode(ResultCode.SUCCESS).build();
                mav.addObject("success", successResult);
            } catch (Exception e) {
                String errorCode = ErrorCode.RESET_PASSWORD_ERROR.toString();
                errors.reject(errorCode, "Could not reset password");
                String logMessage = MessageFormat.format("action=resetPassword, status=error, token={0}, errorCode={1}, error={2}", resetPasswordData.getToken(), errorCode,
                        e.getMessage());
                logger.error(logMessage, e);
            }
        }
        return mav;
    }

    @InitBinder("recoveryEmailData")
    public void setupRecoveryEmailDataDataBinder(WebDataBinder binder) {
        binder.addValidators(recoveryEmailDataValidator);
    }

    @InitBinder("resetPasswordData")
    public void setupResetPasswordDataDataBinder(WebDataBinder binder) {
        binder.addValidators(resetPasswordDataValidator);
    }

}
