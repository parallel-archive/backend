package hu.codeandsoda.osa.collection.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import hu.codeandsoda.osa.collection.sevice.CollectionService;
import hu.codeandsoda.osa.collection.validator.DeleteCollectionRequestDataValidator;
import hu.codeandsoda.osa.exception.ValidationException;
import hu.codeandsoda.osa.general.data.GenericResponse;
import hu.codeandsoda.osa.general.data.ResultCode;
import io.swagger.annotations.ApiOperation;

@RestController
public class DeleteCollectionController {

    private static final String COLLECTION_URL = "/api/collection/{id}";

    @Autowired
    private CollectionService collectionService;

    @Autowired
    private DeleteCollectionRequestDataValidator deleteCollectionRequestDataValidator;

    @ApiOperation(value = "Delete collection")
    @DeleteMapping(COLLECTION_URL)
    public GenericResponse deleteCollection(Authentication authentication, @PathVariable Long id) throws ValidationException {

        Errors errors = new BeanPropertyBindingResult(id, "collection");
        deleteCollectionRequestDataValidator.validate(id, errors);
        if (errors.hasErrors()) {
            throw new ValidationException("Invalid delete collection request.", errors.getAllErrors());
        }

        collectionService.deleteById(id);

        GenericResponse successResult = new GenericResponse.GenericResponseBuilder().setResultCode(ResultCode.SUCCESS).build();
        return successResult;
    }

}
