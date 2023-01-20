package hu.codeandsoda.osa.scheduled.service;

import java.text.MessageFormat;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;

import hu.codeandsoda.osa.account.user.domain.User;
import hu.codeandsoda.osa.document.data.GenerateOcrRequestData;
import hu.codeandsoda.osa.document.domain.Document;
import hu.codeandsoda.osa.document.domain.DocumentImage;
import hu.codeandsoda.osa.document.service.DocumentService;
import hu.codeandsoda.osa.documentpublish.service.PublishedDocumentService;
import hu.codeandsoda.osa.general.data.ResponseMessage;
import hu.codeandsoda.osa.jms.config.ActiveMQConfig;
import hu.codeandsoda.osa.jms.service.documentpublish.PublishDocumentGenerateHashMessageService;
import hu.codeandsoda.osa.media.exception.MediaLoadingException;
import hu.codeandsoda.osa.ocr.exception.GenerateOcrError;
import hu.codeandsoda.osa.ocr.service.OcrService;
import hu.codeandsoda.osa.prometheus.service.PrometheusReporterService;
import hu.codeandsoda.osa.util.ErrorCode;
import hu.codeandsoda.osa.util.OsaConstantUtil;

@Service
public class ScheduledOcrGenerationService {

    private static final String GENERATE_OCR_ACTION = "Generate OCR from document images";

    private static Logger logger = LoggerFactory.getLogger(ScheduledOcrGenerationService.class);

    @Autowired
    private JmsTemplate jmsTemplate;

    @Autowired
    private DocumentService documentService;

    @Autowired
    private PublishedDocumentService publishedDocumentService;

    @Autowired
    private OcrService ocrService;

    @Autowired
    private PrometheusReporterService prometheusReporterService;

    @Autowired
    private PublishDocumentGenerateHashMessageService publishDocumentGenerateHashMessageService;

    @Async("asyncDocumentPublishExecutor")
    public void generateOcr() {
        final String queue = ActiveMQConfig.GENERATE_OCR_QUEUE;
        GenerateOcrRequestData generateOcrRequestData = (GenerateOcrRequestData) jmsTemplate.receiveAndConvert(queue);

        if (null != generateOcrRequestData) {
            Long documentId = generateOcrRequestData.getDocumentId();
            Document document = documentService.loadById(documentId);
            User user = document.getUser();
            
            ZonedDateTime start = ZonedDateTime.now();
            String logStart = MessageFormat.format("START {0}. Document ID: {1}. Current time is: {2}", GENERATE_OCR_ACTION, documentId, start);
            logger.info(logStart);

            try {
                Errors errors = new BeanPropertyBindingResult(document, "document");
                List<ResponseMessage> responseMessages = generateImageOcrs(document, user, errors);

                Long publishedDocumentId = generateOcrRequestData.getPublishedDocumentId();
                if (null != publishedDocumentId) {
                    if (!responseMessages.isEmpty()) {
                        handleGenerateOcrError(documentId, errors);
                    }

                    publishedDocumentService.constructPublishedDocumentOcrsAndSave(publishedDocumentId, documentId);

                    publishDocumentGenerateHashMessageService.addToQueue(publishedDocumentId);
                }

                long duration = Duration.between(start, ZonedDateTime.now()).getSeconds();
                String logEnd = MessageFormat.format("END {0}. Document ID: {1}. Duration: {2} seconds.", GENERATE_OCR_ACTION, documentId, duration);
                logger.info(logEnd);
            } catch (Exception e) {
                handleGenerateOcrQueueError(GENERATE_OCR_ACTION, queue, generateOcrRequestData, e);
            }

        } else {
            String log = MessageFormat.format(OsaConstantUtil.NO_ELEMENT_IN_QUEUE_LOG_MESSAGE, queue);
            logger.info(log);
        }

    }

    private List<ResponseMessage> generateImageOcrs(Document document, User user, Errors errors) throws MediaLoadingException, GenerateOcrError {
        Long documentId = document.getId();
        List<DocumentImage> documentImages = document.getImages();
        List<ResponseMessage> responseMessages = ocrService.generateImageOcrs(documentImages, documentId, user, errors);
        return responseMessages;
    }

    private void handleGenerateOcrError(Long documentId, Errors errors) throws GenerateOcrError {
        String errorCode = ErrorCode.GENERATE_OCR_ERROR.toString();

        String logMessage = MessageFormat.format("action=generateOcr, status=error, documentId={0}, errorCode={1}", documentId, errorCode);
        logger.error(logMessage);

        prometheusReporterService.sendGenerateOcrErrorWithErrors(errors.getAllErrors());

        String errorMessage = "Could not generate OCR from document images.";
        errors.reject(errorCode, errorMessage);
        throw new GenerateOcrError(errorMessage, errors.getAllErrors());
    }

    private void handleGenerateOcrQueueError(String action, String queue, GenerateOcrRequestData generateOcrRequestData, Exception exception) {
        Long documentId = generateOcrRequestData.getDocumentId();

        String errorLog = MessageFormat.format("ERROR action={0}, documentId={1}, requeue-to={2}, error={3}", action, documentId, queue, exception.getMessage());
        logger.error(errorLog, exception);

        prometheusReporterService.sendGenerateOcrQueueErrorCounter(documentId, queue, exception.getMessage());

        jmsTemplate.convertAndSend(queue, generateOcrRequestData);
    }

}
