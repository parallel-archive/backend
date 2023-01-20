package hu.codeandsoda.osa.account.user.web;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.text.MessageFormat;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import hu.codeandsoda.osa.account.user.data.UserAccountData;
import hu.codeandsoda.osa.account.user.data.UserData;
import hu.codeandsoda.osa.account.user.data.UserDisplayNameRequestData;
import hu.codeandsoda.osa.account.user.data.UserMenuData;
import hu.codeandsoda.osa.account.user.domain.User;
import hu.codeandsoda.osa.account.user.exception.LoadUserDataException;
import hu.codeandsoda.osa.account.user.exception.UserDeletionException;
import hu.codeandsoda.osa.general.data.GenericResponse;
import hu.codeandsoda.osa.general.data.ResultCode;
import hu.codeandsoda.osa.security.service.UserService;
import hu.codeandsoda.osa.util.ErrorCode;
import hu.codeandsoda.osa.util.ResourceHandler;
import io.swagger.annotations.ApiOperation;

@RestController
public class UserController {
    
    public static final String LOGIN_USER_URL = "/api/user/active";
    public static final String DELETE_USER_URL = "/api/user/delete";
    public static final String LOAD_USER_DATA_URL = "/api/user/data";
    public static final String LOAD_USER_MENU_DATA = "/api/user/menu";
    public static final String USER_EMAIL_SETTING_URL = "/api/user/email/public";

    private static final String LOAD_USER_ACCOUNT_DATA_ACTION = "loadUserAccountData";

    private static Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private HttpServletResponse response;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ResourceHandler resourceHandler;

    @ApiOperation(value = "Load active user")
    @GetMapping(LOGIN_USER_URL)
    public UserData loadActiveUser() {
        UserData user = null;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (null != authentication && null != authentication.getPrincipal() && authentication.getPrincipal() instanceof User) {
            String email = ((User) authentication.getPrincipal()).getEmail();
            user = new UserData.UserDataBuilder().setName(email).build();
        }
        
        return user;
    }

    @ApiOperation(value = "Delete active user")
    @DeleteMapping(DELETE_USER_URL)
    public void deleteUser(Authentication authentication) throws UserDeletionException {
        User user = (User) authentication.getPrincipal();
        Long userId = user.getId();
        Errors errors = new BeanPropertyBindingResult(user, "user");

        try {
            userService.deleteUser(user, errors);

            String logMessage = MessageFormat.format("action=deleteUser, status=success, userId={0}", userId);
            logger.info(logMessage);
        } catch (Exception e) {
            String errorLog = MessageFormat.format("action=deleteUser, status=failed, userId={0}, error={1}", userId, e.getMessage());
            logger.error(errorLog, e);
            
            errors.reject(ErrorCode.USER_DELETE_ERROR.toString(), "Could not delete user");
            throw new UserDeletionException("Could not delete user", errors.getAllErrors());
        }

        new SecurityContextLogoutHandler().logout(request, response, authentication);
    }

    @ApiOperation(value = "Load user data")
    @GetMapping(LOAD_USER_DATA_URL)
    public void loadUserAccountData(HttpServletResponse response, Authentication authentication) throws LoadUserDataException {
        User user = (User) authentication.getPrincipal();
        Errors errors = new BeanPropertyBindingResult(user, "user");

        UserAccountData userAccountData = userService.collectUserAccountData(user);

        final Long userId = user.getId();
        String fileName = "userdata_" + userId;

        File temporaryFile = null;
        OutputStream out = null;
        FileInputStream in = null;
        try {
            temporaryFile = File.createTempFile(fileName, ".txt");
            out = response.getOutputStream();
            in = new FileInputStream(temporaryFile);

            objectMapper.registerModule(new JavaTimeModule());
            objectMapper.writeValue(temporaryFile, userAccountData);

            response.setContentType("application/txt");
            response.setHeader("Content-disposition", "attachment; filename=" + temporaryFile.getName());

            IOUtils.copy(in, out);
            temporaryFile.delete();

        } catch (Exception e) {
            handleLoadUserAccountDataError(userId, e, errors);
        } finally {
            String actionParameters = MessageFormat.format("userId={0}", userId);
            resourceHandler.closeOutputStream(out, LOAD_USER_ACCOUNT_DATA_ACTION, actionParameters);
            resourceHandler.closeInputStream(in, LOAD_USER_ACCOUNT_DATA_ACTION, actionParameters);
        }
    }

    @ApiOperation(value = "Load user menu data")
    @GetMapping(LOAD_USER_MENU_DATA)
    public UserMenuData loadUserMenuData() {
        UserMenuData userMenuData = userService.loadUserMenuData();
        if (null == userMenuData) {
            userMenuData = new UserMenuData();
        }
        return userMenuData;
    }

    @ApiOperation(value = "Set user e-mail status")
    @PostMapping(USER_EMAIL_SETTING_URL)
    public GenericResponse setUserEmailStatus(@RequestBody UserDisplayNameRequestData userDisplayNameRequestData) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        userService.setUserEmailStatus(user.getId(), userDisplayNameRequestData.isPublicEmail());

        GenericResponse successResult = new GenericResponse.GenericResponseBuilder().setResultCode(ResultCode.SUCCESS).build();
        return successResult;
    }

    private void handleLoadUserAccountDataError(Long userId, Exception exception, Errors errors) throws LoadUserDataException {
        String errorLog = MessageFormat.format("action={0}, status=error, userId={1}, error={2}", LOAD_USER_ACCOUNT_DATA_ACTION, userId, exception.getMessage());
        logger.error(errorLog, exception);

        String errorMessage = MessageFormat.format("Could not load User account data with ID = {0}", userId);
        throw new LoadUserDataException(errorMessage, errors.getAllErrors());
    }

}
