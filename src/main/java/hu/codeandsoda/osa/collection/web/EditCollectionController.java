package hu.codeandsoda.osa.collection.web;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import hu.codeandsoda.osa.account.user.domain.User;
import hu.codeandsoda.osa.collection.data.CollectionContentRequestData;
import hu.codeandsoda.osa.collection.data.EditCollectionRequestData;
import hu.codeandsoda.osa.collection.sevice.CollectionService;
import hu.codeandsoda.osa.collection.validator.AddCollectionContentRequestDataValidator;
import hu.codeandsoda.osa.collection.validator.EditCollectionRequestDataValidator;
import hu.codeandsoda.osa.collection.validator.RemoveCollectionContentRequestDataValidator;
import hu.codeandsoda.osa.exception.ValidationException;
import hu.codeandsoda.osa.general.data.GenericResponse;
import hu.codeandsoda.osa.general.data.ResultCode;
import io.swagger.annotations.ApiOperation;

@RestController
public class EditCollectionController {
    
    public static final String COLLECTION_URL = "/api/collection";
    private static final String COLLECTION_PUBLISHED_DOCUMENT_URL = "/api/collection/{collectionId}/publication/{publicationHash}";

    @Autowired
    private CollectionService collectionService;

    @Autowired
    private AddCollectionContentRequestDataValidator addCollectionContentRequestDataValidator;

    @Autowired
    private RemoveCollectionContentRequestDataValidator removeCollectionContentRequestDataValidator;

    @Autowired
    private EditCollectionRequestDataValidator editCollectionRequestDataValidator;

    @ApiOperation(value = "Add published document to collection")
    @PutMapping(COLLECTION_PUBLISHED_DOCUMENT_URL)
    public GenericResponse addPublichedDocumentToCollection(Authentication authentication, @PathVariable Long collectionId, @PathVariable String publicationHash)
            throws ValidationException {

        CollectionContentRequestData collectionContentRequestData = new CollectionContentRequestData.CollectionContentRequestDataBuilder().setCollectionId(collectionId)
                .setPublishedDocumentHash(publicationHash).build();
        Errors errors = new BeanPropertyBindingResult(collectionContentRequestData, "collectionContentRequestData");
        addCollectionContentRequestDataValidator.validate(collectionContentRequestData, errors);

        if (errors.hasErrors()) {
            throw new ValidationException("Invalid remove published document from collection request.", errors.getAllErrors());
        }

        Long userId = ((User) authentication.getPrincipal()).getId();
        collectionService.addPublishedDocumentToCollection(collectionId, publicationHash, userId);

        GenericResponse successResult = new GenericResponse.GenericResponseBuilder().setResultCode(ResultCode.SUCCESS).build();
        return successResult;
    }

    @ApiOperation(value = "Remove published document from collection")
    @DeleteMapping(COLLECTION_PUBLISHED_DOCUMENT_URL)
    public GenericResponse deletePublichedDocumentFromCollection(Authentication authentication, @PathVariable Long collectionId, @PathVariable String publicationHash)
            throws ValidationException {

        CollectionContentRequestData collectionContentRequestData = new CollectionContentRequestData.CollectionContentRequestDataBuilder().setCollectionId(collectionId)
                .setPublishedDocumentHash(publicationHash).build();
        Errors errors = new BeanPropertyBindingResult(collectionContentRequestData, "collectionContentRequestData");
        removeCollectionContentRequestDataValidator.validate(collectionContentRequestData, errors);

        if (errors.hasErrors()) {
            throw new ValidationException("Invalid add published document to collection request.", errors.getAllErrors());
        }

        Long userId = ((User) authentication.getPrincipal()).getId();
        collectionService.deletePublishedDocumentFromCollection(collectionId, publicationHash, userId);

        GenericResponse successResult = new GenericResponse.GenericResponseBuilder().setResultCode(ResultCode.SUCCESS).build();
        return successResult;
    }

    @ApiOperation(value = "Edit collection")
    @PutMapping(COLLECTION_URL)
    public GenericResponse deletePublichedDocumentFromCollection(Authentication authentication, @Valid @RequestBody EditCollectionRequestData editCollectionRequestData,
            Errors errors) throws ValidationException {

        if (errors.hasErrors()) {
            throw new ValidationException("Invalid edit collection request.", errors.getAllErrors());
        }

        collectionService.editCollection(editCollectionRequestData);

        GenericResponse successResult = new GenericResponse.GenericResponseBuilder().setResultCode(ResultCode.SUCCESS).build();
        return successResult;
    }

    @InitBinder("editCollectionRequestData")
    public void setupEditCollectionRequestDataDataBinder(WebDataBinder binder) {
        binder.addValidators(editCollectionRequestDataValidator);
    }

}
