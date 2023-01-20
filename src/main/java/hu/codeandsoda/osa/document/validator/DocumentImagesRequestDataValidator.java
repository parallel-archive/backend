package hu.codeandsoda.osa.document.validator;

import java.text.MessageFormat;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import hu.codeandsoda.osa.account.user.domain.User;
import hu.codeandsoda.osa.document.data.DocumentImagesRequestData;
import hu.codeandsoda.osa.document.domain.Document;
import hu.codeandsoda.osa.document.service.DocumentService;
import hu.codeandsoda.osa.myshoebox.service.ImageService;
import hu.codeandsoda.osa.util.ErrorCode;

@Component
public class DocumentImagesRequestDataValidator implements Validator {

    private static Logger logger = LoggerFactory.getLogger(DocumentImagesRequestDataValidator.class);

    @Autowired
    private ImageService imageService;

    @Autowired
    private DocumentService documentService;

    @Override
    public boolean supports(Class<?> clazz) {
        return DocumentImagesRequestData.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        DocumentImagesRequestData documentImagesRequestData = (DocumentImagesRequestData) target;
        Long documentId = documentImagesRequestData.getId();
        List<Long> imageIds = documentImagesRequestData.getImageIds();

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = user.getId();

        if (null != documentId) {
            validateIfDocumentExists(documentId, userId, errors);

            if (!errors.hasErrors()) {
                validateDocumentStatus(documentId, errors);
            }
        }

        if (!errors.hasErrors()) {
            Long myShoeBoxId = user.getMyShoeBox().getId();
            for (Long imageId : imageIds) {
                validateIfImageExists(imageId, userId, myShoeBoxId, errors);
            }
        }

    }

    private boolean validateIfDocumentExists(Long documentId, Long userId, Errors errors) {
        boolean documentExists = documentService.userDocumentExist(documentId, userId);
        if (!documentExists) {
            String errorCode = ErrorCode.DOCUMENT_NOT_FOUND.toString();
            String logMessage = new StringBuilder().append("action=saveDocumentImages, status=error, userId=").append(userId).append(", documentId=").append(documentId)
                    .append(", errorCode=").append(errorCode).toString();
            errors.reject(errorCode, "Invalid document ID.");
            logger.error(logMessage);
        }
        return documentExists;
    }

    private void validateDocumentStatus(Long documentId, Errors errors) {
        Document document = documentService.loadById(documentId);
        boolean publishInProgress = documentService.isPublishInProgress(document);
        if (publishInProgress) {
            Long userId = document.getUser().getId();
            Long publishedDocumentId = document.getPublishedDocument().getId();

            String errorCode = ErrorCode.DOCUMENT_ALREADY_PUBLISHED.toString();
            errors.reject(errorCode, "Invalid document ID.");

            String logMessage = MessageFormat.format("action=saveDocumentImages, status=error, userId={0}, documentId={1}, publishedDocumentId={2}, errorCode={3}", userId,
                    documentId,
                    publishedDocumentId, errorCode);
            logger.error(logMessage);
        }
    }

    private void validateIfImageExists(Long imageId, Long userId, Long myShoeBoxId, Errors errors) {
        boolean imageExists = imageService.userImageExists(imageId, myShoeBoxId);
        if (!imageExists) {
            String errorCode = ErrorCode.INVALID_IMAGE_ID.toString();
            String logMessage = new StringBuilder().append("action=saveDocumentImages, status=error, userId=").append(userId).append(", myShoeBoxId=").append(myShoeBoxId)
                    .append(", imageId=").append(imageId).append(", errorCode=").append(errorCode).toString();
            errors.reject(errorCode, "Invalid image ID.");
            logger.error(logMessage);
        }
    }
}
