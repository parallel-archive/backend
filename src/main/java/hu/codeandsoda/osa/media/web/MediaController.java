package hu.codeandsoda.osa.media.web;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import hu.codeandsoda.osa.account.user.domain.User;
import hu.codeandsoda.osa.exception.ValidationException;
import hu.codeandsoda.osa.general.data.GenericResponse;
import hu.codeandsoda.osa.general.data.ResultCode;
import hu.codeandsoda.osa.media.exception.MediaUploadException;
import hu.codeandsoda.osa.media.service.MediaService;
import hu.codeandsoda.osa.media.validation.DeleteImagesRequestDataValidator;
import hu.codeandsoda.osa.media.validation.MultipartFileValidator;
import hu.codeandsoda.osa.myshoebox.data.DeleteImagesRequestData;
import hu.codeandsoda.osa.myshoebox.data.ImageData;
import io.swagger.annotations.ApiOperation;

@RestController
public class MediaController {

	public static final String MEDIA_URL = "/api/media";
	public static final String DELETE_IMAGES_URL = "/api/images/delete";

	@Autowired
	private MediaService mediaService;

	@Autowired
	private MultipartFileValidator multipartFileValidator;

    @Autowired
    private DeleteImagesRequestDataValidator deleteImagesRequestDataValidator;

    @ApiOperation(value = "Upload MultipartFile")
	@PostMapping(MEDIA_URL)
	public ImageData uploadMedia(@RequestParam("file") MultipartFile file)
            throws MediaUploadException, ValidationException {
		Errors errors = new BeanPropertyBindingResult(file, "file");
		multipartFileValidator.validate(file, errors);

		if (errors.hasErrors()) {
			throw new ValidationException("File upload validation error.", errors.getAllErrors());
		}

		ImageData imageData = mediaService.uploadImageWithThumbnail(file, errors);
		return imageData;
	}

    @ApiOperation(value = "Delete images")
    @DeleteMapping(DELETE_IMAGES_URL)
    public GenericResponse deleteImages(Authentication authentication, @Valid @RequestBody DeleteImagesRequestData deleteImagesRequestData, Errors errors)
            throws ValidationException {
        if (errors.hasErrors()) {
            throw new ValidationException("Invalid delete images input.", errors.getAllErrors());
        }

        User user = (User) authentication.getPrincipal();
        mediaService.deleteImages(deleteImagesRequestData.getImageIds(), user);

        return new GenericResponse.GenericResponseBuilder().setResultCode(ResultCode.SUCCESS).build();
    }

    @InitBinder("deleteImagesRequestData")
    public void setupDeleteImagesRequestDataDataBinder(WebDataBinder binder) {
        binder.addValidators(deleteImagesRequestDataValidator);
    }
}
