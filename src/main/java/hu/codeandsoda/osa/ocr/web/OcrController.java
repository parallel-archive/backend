package hu.codeandsoda.osa.ocr.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import hu.codeandsoda.osa.account.user.domain.User;
import hu.codeandsoda.osa.document.data.DocumentData;
import hu.codeandsoda.osa.document.service.DocumentService;
import hu.codeandsoda.osa.exception.ValidationException;
import hu.codeandsoda.osa.general.data.ResponseMessage;
import hu.codeandsoda.osa.media.exception.MediaLoadingException;
import hu.codeandsoda.osa.ocr.exception.GenerateOcrError;
import hu.codeandsoda.osa.ocr.service.OcrService;
import hu.codeandsoda.osa.ocr.validation.OcrRequestValidator;

@RestController
public class OcrController {

    private static final String OCR_URL = "/api/document/{id}/ocr";

    @Autowired
    private OcrService ocrService;

    @Autowired
    private DocumentService documentService;

    @Autowired
    private OcrRequestValidator ocrRequestValidator;

    @PostMapping(OCR_URL)
    public DocumentData generateOcr(Authentication authentication, @PathVariable Long id) throws ValidationException, MediaLoadingException, GenerateOcrError {
        User user = (User) authentication.getPrincipal();

        Errors errors = new BeanPropertyBindingResult(id, "id");
        ocrRequestValidator.validate(id, errors);
        if (errors.hasErrors()) {
            throw new ValidationException("Invalid generate OCR from document images request.", errors.getAllErrors());
        }

        List<ResponseMessage> responseMessages = ocrService.generateOcrFromDocumentImages(id, user, errors);

        DocumentData document = documentService.loadDocumentDataById(id);
        document.setMessages(responseMessages);
        return document;
    }

}
