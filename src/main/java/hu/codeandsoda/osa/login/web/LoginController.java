package hu.codeandsoda.osa.login.web;

import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import io.swagger.annotations.ApiOperation;

@Controller
public class LoginController {

    public static final String LOGIN_URL = "/login";

    private static final String LOGIN_TEMPLATE = "login";

    @ApiOperation(value = "Load login form")
    @GetMapping(LOGIN_URL)
    public ModelAndView loginForm(@RequestParam(required = false) String targetUrl) {
        ModelAndView mav = new ModelAndView(LOGIN_TEMPLATE);
        
        if (StringUtils.hasText(targetUrl)) {
            mav.addObject("targetUrl", targetUrl);
        }
        
        return mav;
    }

}
