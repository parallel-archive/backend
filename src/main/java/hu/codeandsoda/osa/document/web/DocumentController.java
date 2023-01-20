package hu.codeandsoda.osa.document.web;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import hu.codeandsoda.osa.account.user.domain.User;
import hu.codeandsoda.osa.document.data.DocumentData;
import hu.codeandsoda.osa.document.data.DocumentImagesRequestData;
import hu.codeandsoda.osa.document.data.DocumentRequestData;
import hu.codeandsoda.osa.document.data.DocumentsData;
import hu.codeandsoda.osa.document.domain.Document;
import hu.codeandsoda.osa.document.service.DocumentService;
import hu.codeandsoda.osa.document.validator.DocumentImagesRequestDataValidator;
import hu.codeandsoda.osa.document.validator.DocumentRequestDataValidator;
import hu.codeandsoda.osa.document.validator.PublishDocumentRequestValidator;
import hu.codeandsoda.osa.documentpublish.data.DocumentPublishResponseData;
import hu.codeandsoda.osa.documentpublish.service.PublishedDocumentService;
import hu.codeandsoda.osa.exception.ValidationException;
import hu.codeandsoda.osa.general.data.GenericResponse;
import hu.codeandsoda.osa.general.data.ResultCode;
import hu.codeandsoda.osa.media.exception.MediaCopyException;
import io.swagger.annotations.ApiOperation;

@RestController
public class DocumentController {

    public static final String DOCUMENT_IMAGES_URL = "/api/document/images";
    public static final String DOCUMENT_URL = "/api/document/{id}";
    public static final String DOCUMENTS_URL = "/api/documents";

    @Autowired
    private DocumentService documentService;

    @Autowired
    private PublishedDocumentService publishedDocumentService;

    @Autowired
    private DocumentImagesRequestDataValidator documentImagesRequestDataValidator;

    @Autowired
    private DocumentRequestDataValidator documentRequestDataValidator;

    @Autowired
    private PublishDocumentRequestValidator publishDocumentRequestValidator;

    @ApiOperation(value = "Add document images to new or existing document")
    @PostMapping(DOCUMENT_IMAGES_URL)
    public DocumentData saveDocumentImages(Authentication authentication, @Valid @RequestBody DocumentImagesRequestData documentImagesRequestData, Errors errors)
            throws ValidationException, MediaCopyException {
        if (errors.hasErrors()) {
            throw new ValidationException("Invalid save document images request.", errors.getAllErrors());
        }
        User user = (User) authentication.getPrincipal();
        DocumentData documentData = documentService.saveDocumentImages(user, documentImagesRequestData, errors);
        return documentData;
    }

    @ApiOperation(value = "Load document by ID")
    @GetMapping(DOCUMENT_URL)
    public DocumentData loadDocumentById(Authentication authentication, @PathVariable Long id) throws ValidationException {
        Errors errors = new BeanPropertyBindingResult(id, "document");
        User user = (User) authentication.getPrincipal();

        DocumentData document = documentService.loadEditableDocumentByIdAndUser(id, user, errors);
        return document;
    }

    @ApiOperation(value = "Delete document by ID")
    @DeleteMapping(DOCUMENT_URL)
    public GenericResponse deleteDocumentById(Authentication authentication, @PathVariable Long id) throws ValidationException {
        Errors errors = new BeanPropertyBindingResult(id, "document");
        User user = (User) authentication.getPrincipal();

        documentService.deleteByIdAndUser(id, user.getId(), errors);
        return new GenericResponse.GenericResponseBuilder().setResultCode(ResultCode.SUCCESS).build();
    }

    @ApiOperation(value = "Edit document")
    @PutMapping(DOCUMENT_URL)
    public DocumentData editDocument(@Valid @RequestBody DocumentRequestData documentRequestData, Errors errors) throws ValidationException {
        if (errors.hasErrors()) {
            throw new ValidationException("Invalid edit document request.", errors.getAllErrors());
        }

        DocumentData document = documentService.editDocument(documentRequestData, errors);
        return document;
    }

    @ApiOperation(value = "Load documents by user ID")
    @GetMapping(DOCUMENTS_URL)
    public DocumentsData loadDocuments(Authentication authentication, @RequestParam(defaultValue = "DESC") String sort, @RequestParam(defaultValue = "DATE") String sortBy,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "0") int page) {
        User user = (User) authentication.getPrincipal();
        DocumentsData documents = documentService.loadEditableDocuments(user, sort, sortBy, size, page);
        return documents;
    }

    @ApiOperation(value = "Publish document")
    @PostMapping(DOCUMENT_URL)
    public DocumentPublishResponseData publishDocument(Authentication authentication, @PathVariable Long id)
            throws ValidationException {
        Document document = documentService.loadById(id);

        Errors errors = new BeanPropertyBindingResult(document, "document");
        publishDocumentRequestValidator.validate(document, errors);
        if (errors.hasErrors()) {
            throw new ValidationException("Invalid publish document request.", errors.getAllErrors());
        }

        User user = (User) authentication.getPrincipal();
        DocumentPublishResponseData response = publishedDocumentService.addToPublishDocumentQueue(document, user, errors);
        return response;
    }

    @InitBinder("documentImagesRequestData")
    public void setupDocumentImagesRequestDataDataBinder(WebDataBinder binder) {
        binder.addValidators(documentImagesRequestDataValidator);
    }

    @InitBinder("documentRequestData")
    public void setupDocumentRequestDataDataBinder(WebDataBinder binder) {
        binder.addValidators(documentRequestDataValidator);
    }

}
