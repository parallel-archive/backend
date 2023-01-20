package hu.codeandsoda.osa.myshoebox.web;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import hu.codeandsoda.osa.account.user.domain.User;
import hu.codeandsoda.osa.exception.ValidationException;
import hu.codeandsoda.osa.media.exception.MediaCopyException;
import hu.codeandsoda.osa.media.exception.MediaLoadingException;
import hu.codeandsoda.osa.media.exception.MediaUploadException;
import hu.codeandsoda.osa.media.service.MediaService;
import hu.codeandsoda.osa.myshoebox.data.ImageData;
import hu.codeandsoda.osa.myshoebox.data.ImageRequestData;
import hu.codeandsoda.osa.myshoebox.data.MyShoeBoxData;
import hu.codeandsoda.osa.myshoebox.service.MyShoeBoxService;
import hu.codeandsoda.osa.myshoebox.validation.ImageRequestDataValidator;
import io.swagger.annotations.ApiOperation;

@RestController
public class MyShoeBoxController {

    public static final String USER_MYSHOEBOX_URL = "/api/user/myshoebox";
    public static final String IMAGE_URL = "/api/image/{id}";

    @Autowired
    private MyShoeBoxService myShoeBoxService;

    @Autowired
    private MediaService mediaService;

    @Autowired
    private ImageRequestDataValidator imageRequestDataValidator;

    @ApiOperation(value = "Load MyShoeBox by User ID")
    @GetMapping(USER_MYSHOEBOX_URL)
    public MyShoeBoxData loadUserMyShoeBox(Authentication authentication, @RequestParam(defaultValue = "DESC") String sort, @RequestParam(defaultValue = "DATE") String sortBy,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "0") int page) {
        User user = (User) authentication.getPrincipal();
        MyShoeBoxData myShoeBoxData = myShoeBoxService.loadUserMyShoeBox(user.getMyShoeBox().getId(), sort, sortBy, size, page);
        return myShoeBoxData;
    }
    
    @ApiOperation(value = "Load image by ID")
    @GetMapping(IMAGE_URL)
    public ImageData loadImageById(Authentication authentication, @PathVariable Long id) throws ValidationException {
        Errors errors = new BeanPropertyBindingResult(id, "image");
        User user = (User) authentication.getPrincipal();

        ImageData image = myShoeBoxService.loadImageByIdAndUser(id, user, errors);
        return image;
    }

    @ApiOperation(value = "Edit image")
    @PutMapping(IMAGE_URL)
    public ImageData editImage(@Valid @RequestBody ImageRequestData imageRequestData, Errors errors)
            throws ValidationException, MediaUploadException, MediaLoadingException, MediaCopyException {
        if (errors.hasErrors()) {
            throw new ValidationException("Invalid edit image request.", errors.getAllErrors());
        }

        ImageData image = mediaService.editImage(imageRequestData, errors);
        return image;
    }
    
    @InitBinder("imageRequestData")
    public void setupImageRequestDataDataBinder(WebDataBinder binder) {
        binder.addValidators(imageRequestDataValidator);
    }

}
