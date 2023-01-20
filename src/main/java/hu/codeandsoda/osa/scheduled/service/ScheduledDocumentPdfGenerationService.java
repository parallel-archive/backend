package hu.codeandsoda.osa.scheduled.service;

import java.text.MessageFormat;
import java.time.Duration;
import java.time.ZonedDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;

import hu.codeandsoda.osa.documentpublish.service.PublishedDocumentService;
import hu.codeandsoda.osa.jms.config.ActiveMQConfig;
import hu.codeandsoda.osa.jms.service.documentpublish.PublishDocumentUploadToIpfsMessageService;
import hu.codeandsoda.osa.jms.service.error.JmsErrorHandler;
import hu.codeandsoda.osa.util.OsaConstantUtil;

@Service
public class ScheduledDocumentPdfGenerationService {

    private static final String GENERATE_PDF_ACTION = "Generate PDF from document and upload";

    private static Logger logger = LoggerFactory.getLogger(ScheduledDocumentPdfGenerationService.class);

    @Autowired
    private JmsTemplate jmsTemplate;

    @Autowired
    private PublishedDocumentService publishedDocumentService;

    @Autowired
    private PublishDocumentUploadToIpfsMessageService publishDocumentUploadToIpfsMessageService;

    @Autowired
    private JmsErrorHandler jmsErrorHandler;

    @Async("asyncDocumentPDFGeneratorExecutor")
    public void generatePublishedDocumentPdf() {
        final String queue = ActiveMQConfig.PUBLISH_DOCUMENT_GENERATE_PDF_QUEUE;
        Long publishedDocumentId = (Long) jmsTemplate.receiveAndConvert(queue);

        if (null != publishedDocumentId) {
            ZonedDateTime start = ZonedDateTime.now();
            String logStart = MessageFormat.format("START Publish document: {0}. Published Document ID: {1}. Current time is: {2}", GENERATE_PDF_ACTION,
                    publishedDocumentId, start);
            logger.info(logStart);

            Errors errors = new BeanPropertyBindingResult(publishedDocumentId, "publishDocumentId");
            try {
                publishedDocumentService.generateAndUploadPdf(publishedDocumentId, errors);

                publishDocumentUploadToIpfsMessageService.addToQueue(publishedDocumentId);

                long duration = Duration.between(start, ZonedDateTime.now()).getSeconds();
                String logEnd = MessageFormat.format("END Publish document: {0}. Published document ID: {1}. Duration: {2} seconds.", GENERATE_PDF_ACTION,
                        publishedDocumentId, duration);
                logger.info(logEnd);

            } catch (Exception e) {
                jmsErrorHandler.handleDocumentPublishError(GENERATE_PDF_ACTION, queue, publishedDocumentId, e);
            } 
        } else {
            String log = MessageFormat.format(OsaConstantUtil.NO_ELEMENT_IN_QUEUE_LOG_MESSAGE, queue);
            logger.info(log);
        }
    }

}
