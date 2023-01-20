package hu.codeandsoda.osa.ipfs.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import hu.codeandsoda.osa.account.user.data.UserMenuData;
import hu.codeandsoda.osa.security.service.UserService;
import io.swagger.annotations.ApiOperation;

@Controller
public class IpfsController {

    public static final String IPFS_HELP_PAGE_URL = "/ipfs";

    private static final String IPFS_HELP_PAGE_TEMPLATE = "ipfs";

    @Autowired
    private UserService userService;

    @ApiOperation(value = "Load IPFS help page")
    @GetMapping(IPFS_HELP_PAGE_URL)
    public ModelAndView loginForm() {
        ModelAndView mav = new ModelAndView(IPFS_HELP_PAGE_TEMPLATE);

        UserMenuData userMenuData = userService.loadUserMenuData();
        if (null != userMenuData) {
            mav.addObject("userMenuData", userMenuData);
        }

        return mav;
    }

}
