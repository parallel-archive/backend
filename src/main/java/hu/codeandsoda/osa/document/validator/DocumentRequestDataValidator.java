package hu.codeandsoda.osa.document.validator;

import java.text.MessageFormat;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import hu.codeandsoda.osa.account.user.domain.User;
import hu.codeandsoda.osa.document.data.DocumentImageRequestData;
import hu.codeandsoda.osa.document.data.DocumentMetaDataRequestData;
import hu.codeandsoda.osa.document.data.DocumentRequestData;
import hu.codeandsoda.osa.document.domain.Document;
import hu.codeandsoda.osa.document.service.DocumentImageService;
import hu.codeandsoda.osa.document.service.DocumentService;
import hu.codeandsoda.osa.util.ErrorCode;

@Component
public class DocumentRequestDataValidator implements Validator {

    private static Logger logger = LoggerFactory.getLogger(DocumentRequestDataValidator.class);
    private static final int INDEX_MIN = 0;
    private static final String PERIOD_COVERED_FROM_FIELD_NAME = "periodCoveredFrom";
    private static final String PERIOD_COVERED_TO_FIELD_NAME = "periodCoveredTo";

    @Autowired
    private DocumentService documentService;

    @Autowired
    private DocumentImageService documentImageService;

    @Override
    public boolean supports(Class<?> clazz) {
        return DocumentRequestData.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        DocumentRequestData documentRequestData = (DocumentRequestData) target;

        Long documentId = documentRequestData.getId();

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = user.getId();

        validateIfDocumentExists(documentId, userId, errors);

        if (!errors.hasErrors()) {
            validateDocumentStatus(documentId, errors);
        }

        if (!errors.hasErrors()) {
            validateDocumentContent(documentId, userId, documentRequestData, errors);
        }
    }



    private void validateIfDocumentExists(Long documentId, Long userId, Errors errors) {
        boolean documentExists = documentService.userDocumentExist(documentId, userId);
        if (!documentExists) {
            String errorCode = ErrorCode.DOCUMENT_NOT_FOUND.toString();
            String logMessage = new StringBuilder().append("action=editDocument, status=error, userId=").append(userId).append(", documentId=").append(documentId)
                    .append(", errorCode=").append(errorCode).toString();
            errors.reject(errorCode, "Invalid document ID.");
            logger.error(logMessage);
        }
    }

    private void validateDocumentStatus(Long documentId, Errors errors) {
        Document document = documentService.loadById(documentId);
        boolean publishInProgress = documentService.isPublishInProgress(document);
        if (publishInProgress) {
            Long userId = document.getUser().getId();
            Long publishedDocumentId = document.getPublishedDocument().getId();

            String errorCode = ErrorCode.DOCUMENT_ALREADY_PUBLISHED.toString();
            errors.reject(errorCode, "Invalid document ID.");

            String logMessage = MessageFormat.format("action=editDocument, status=error, userId={0}, documentId={1}, publishedDocumentId={2}, errorCode={3}", userId, documentId,
                    publishedDocumentId, errorCode);
            logger.error(logMessage);
        }
    }

    private void validateDocumentContent(Long documentId, Long userId, DocumentRequestData documentRequestData, Errors errors) {
        List<DocumentImageRequestData> imageRequestDatas = documentRequestData.getImages();
        validateDocumentImages(documentId, userId, imageRequestDatas, errors);

        DocumentMetaDataRequestData metaDataRequest = documentRequestData.getMetaDataRequest();
        validateDocumentPeriods(documentId, userId, metaDataRequest, errors);
    }

    private void validateDocumentImages(Long documentId, Long userId, List<DocumentImageRequestData> imageRequestDatas, Errors errors) {
        for (DocumentImageRequestData imageRequestData : imageRequestDatas) {
            Long documentImageId = imageRequestData.getImageId();
            validateIfDocumentImageExists(documentImageId, documentId, errors);
        }
        validateDocumentImageIndexDuplicationAndOrder(imageRequestDatas, userId, documentId, errors);
    }

    private void validateIfDocumentImageExists(Long documentImageId, Long documentId, Errors errors) {
        boolean documentImageExists = documentImageService.documentImageExists(documentImageId, documentId);
        if (!documentImageExists) {
            String errorCode = ErrorCode.INVALID_DOCUMENT_IMAGE_ID.toString();
            String logMessage = new StringBuilder().append("action=editDocument, status=error, documentId=").append(documentId).append(", documentImageId=").append(documentImageId)
                    .append(", errorCode=").append(errorCode).toString();
            errors.reject(errorCode, "Invalid document image ID.");
            logger.error(logMessage);
        }
    }

    private void validateDocumentImageIndexDuplicationAndOrder(List<DocumentImageRequestData> imageRequestDatas, Long userId, Long documentId, Errors errors) {
        int indexMax = imageRequestDatas.size();
        List<DocumentImageRequestData> filteredImages = imageRequestDatas.stream().filter(distinctByKey(i -> i.getIndex())).filter(i -> i.getIndex() < indexMax)
                .filter(i -> i.getIndex() >= INDEX_MIN).collect(Collectors.toList());
        if (imageRequestDatas.size() != filteredImages.size()) {
            String errorCode = ErrorCode.INVALID_DOCUMENT_IMAGE_INDEX.toString();
            String logMessage = new StringBuilder().append("action=editDocument, status=error, userId=").append(userId).append(", documentId=").append(documentId)
                    .append(", errorCode=").append(errorCode).toString();
            errors.reject(errorCode, "Document contains invalid or duplicated image indexes.");
            logger.error(logMessage);
        }
    }

    private void validateDocumentPeriods(Long documentId, Long userId, DocumentMetaDataRequestData metaDataRequest, Errors errors) {
        Integer periodCoveredFrom = null;
        if (null != metaDataRequest.getPeriodCoveredFrom()) {
            periodCoveredFrom = metaDataRequest.getPeriodCoveredFrom();
            validatePeriodValue(documentId, userId, periodCoveredFrom, PERIOD_COVERED_FROM_FIELD_NAME, errors);
        }

        if (null != metaDataRequest.getPeriodCoveredTo()) {
            Integer periodCoveredTo = metaDataRequest.getPeriodCoveredTo();
            validatePeriodValue(documentId, userId, periodCoveredTo, PERIOD_COVERED_TO_FIELD_NAME, errors);

            if (null != periodCoveredFrom) {
                validatePeriodValues(documentId, userId, periodCoveredFrom, periodCoveredTo, errors);
            }
        }
    }

    private void validatePeriodValue(Long documentId, Long userId, Integer periodCoveredValue, String fieldName, Errors errors) {
        int currentYear = ZonedDateTime.now().getYear();
        if (periodCoveredValue < 0 || periodCoveredValue > currentYear) {
            String errorCode = ErrorCode.INVALID_DOCUMENT_PERIOD_VALUE.toString();
            String logMessage = MessageFormat.format("action=editDocument, status=error, userId={0}, documentId={1}, errorCode={2}, periodCoveredFieldName={3}, value={4} ", userId,
                    documentId, errorCode, fieldName, periodCoveredValue);

            String errorMessage = MessageFormat.format("Document contains invalid period covered value. Field: {0}", fieldName);
            errors.reject(errorCode, errorMessage);
            logger.error(logMessage);
        }
    }

    private void validatePeriodValues(Long documentId, Long userId, Integer periodCoveredFrom, Integer periodCoveredTo, Errors errors) {
        if (periodCoveredTo < periodCoveredFrom) {
            String errorCode = ErrorCode.INVALID_DOCUMENT_PERIOD_VALUE.toString();
            String logMessage = MessageFormat.format(
                    "action=editDocument, status=error, userId={0}, documentId={1}, errorCode={2}, error= Period covered to value is less than Period covered from value., periodCoveredFrom={3}, periodCoveredTo={4}",
                    userId, documentId, errorCode, periodCoveredFrom, periodCoveredTo);

            errors.reject(errorCode, "Document contains invalid period covered values, To value is less than From value.");
            logger.error(logMessage);
        }
    }

    private <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Map<Object, Boolean> seen = new ConcurrentHashMap<>();
        return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }

}
