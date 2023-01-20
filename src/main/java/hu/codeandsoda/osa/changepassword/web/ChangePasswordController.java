package hu.codeandsoda.osa.changepassword.web;

import java.text.MessageFormat;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import hu.codeandsoda.osa.account.user.data.UserDisplayNameData;
import hu.codeandsoda.osa.account.user.data.UserMenuData;
import hu.codeandsoda.osa.account.user.domain.User;
import hu.codeandsoda.osa.changepassword.data.ChangePasswordData;
import hu.codeandsoda.osa.changepassword.service.ChangePasswordService;
import hu.codeandsoda.osa.changepassword.validation.ChangePasswordDataValidator;
import hu.codeandsoda.osa.general.data.GenericResponse;
import hu.codeandsoda.osa.general.data.ResultCode;
import hu.codeandsoda.osa.security.service.UserService;
import hu.codeandsoda.osa.util.ErrorCode;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;

@Controller
public class ChangePasswordController {

    public static final String CHANGE_PASSWORD_URL = "/changepassword";

    private static final String CHANGE_PASSWORD_TEMPLATE = "change_password";

    private static final String CHANGE_PASSWORD_FORM_NOTES = "Returned model attributes: ChangePasswordData as changePasswordData. See attributes under 4. Definitions.";
    private static final String CHANGE_PASSWORD_SUBMIT_NOTES = "Returned model attributes: GenericResponse as success. See attributes under 4. Definitions.";

    private static Logger logger = LoggerFactory.getLogger(ChangePasswordController.class);

    @Autowired
    private ChangePasswordService changePasswordService;

    @Autowired
    private UserService userService;

    @Autowired
    private ChangePasswordDataValidator changePasswordDataValidator;

    @ApiOperation(value = "Load change password form", notes = CHANGE_PASSWORD_FORM_NOTES)
    @ApiResponse(code = 200, message = CHANGE_PASSWORD_FORM_NOTES, response = ChangePasswordData.class)
    @GetMapping(CHANGE_PASSWORD_URL)
    public ModelAndView changePasswordForm() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        ModelAndView mav = loadChangePasswordTemplate(user.getId());
        mav.addObject("changePasswordData", new ChangePasswordData());

        return mav;
    }

    @ApiOperation(value = "Change password", notes = CHANGE_PASSWORD_SUBMIT_NOTES)
    @ApiResponse(code = 200, message = CHANGE_PASSWORD_SUBMIT_NOTES, response = GenericResponse.class)
    @PostMapping(CHANGE_PASSWORD_URL)
    public ModelAndView changePasswordSubmit(@Valid @ModelAttribute ChangePasswordData changePasswordData, Errors errors) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        ModelAndView mav = loadChangePasswordTemplate(user.getId());
        if (!errors.hasErrors()) {
            try {
                changePasswordService.changePassword(changePasswordData, user);

                String successLogMessage = MessageFormat.format("action=changePassword, status=success, userId={0}", user.getId());
                logger.info(successLogMessage);

                GenericResponse successResult = new GenericResponse.GenericResponseBuilder().setResultCode(ResultCode.SUCCESS).build();
                mav.addObject("success", successResult);
            } catch (Exception e) {
                String errorCode = ErrorCode.CHANGE_PASSWORD_ERROR.toString();
                errors.reject(errorCode, "Could not change password.");
                String logMessage = MessageFormat.format("action=changePassword, status=error, errorCode={0}, error={1}", errorCode, e.getMessage());
                logger.error(logMessage, e);
            }
        }

        return mav;
    }

    @InitBinder("changePasswordData")
    public void setupChangePasswordDataDataBinder(WebDataBinder binder) {
        binder.addValidators(changePasswordDataValidator);
    }

    private ModelAndView loadChangePasswordTemplate(Long userId) {
        ModelAndView mav = new ModelAndView(CHANGE_PASSWORD_TEMPLATE);

        UserMenuData userMenuData = userService.loadUserMenuData();
        if (null != userMenuData) {
            mav.addObject("userMenuData", userMenuData);
        }
        
        UserDisplayNameData userDisplayNameData = userService.loadUserDisplayNameData(userId);
        mav.addObject("userDisplayNameData", userDisplayNameData);

        return mav;
    }

}
