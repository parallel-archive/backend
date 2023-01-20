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
import hu.codeandsoda.osa.jms.service.documentpublish.PublishDocumentDeleteOriginalDocumentMessageService;
import hu.codeandsoda.osa.jms.service.error.JmsErrorHandler;
import hu.codeandsoda.osa.util.OsaConstantUtil;

@Service
public class ScheduledPdfThumbnailCreationService {

    private static final String CREATE_PDF_THUMBNAIL_ACTION = "Create and upload PDF thumbnail";

    private static Logger logger = LoggerFactory.getLogger(ScheduledPdfThumbnailCreationService.class);

    @Autowired
    private JmsTemplate jmsTemplate;

    @Autowired
    private PublishedDocumentService publishedDocumentService;

    @Autowired
    private PublishDocumentDeleteOriginalDocumentMessageService publishDocumentDeleteOriginalDocumentMessageService;

    @Autowired
    private JmsErrorHandler jmsErrorHandler;

    @Async("asyncDocumentPublishExecutor")
    public void createPdfThumbnail() {
        final String queue = ActiveMQConfig.PUBLISH_DOCUMENT_CREATE_PDF_THUMBNAIL_QUEUE;
        Long publishedDocumentId = (Long) jmsTemplate.receiveAndConvert(queue);

        if (null != publishedDocumentId) {
            ZonedDateTime start = ZonedDateTime.now();
            String logStart = MessageFormat.format("START Publish document: {0}. Publish Document ID: {1}. Current time is: {2}", CREATE_PDF_THUMBNAIL_ACTION, publishedDocumentId,
                    start);
            logger.info(logStart);

            Errors errors = new BeanPropertyBindingResult(publishedDocumentId, "publishedDocumentId");
            try {
                publishedDocumentService.uploadPdfThumbnail(publishedDocumentId, errors);

                publishDocumentDeleteOriginalDocumentMessageService.addToQueue(publishedDocumentId);

                long duration = Duration.between(start, ZonedDateTime.now()).getSeconds();
                String logEnd = MessageFormat.format("END Publish document: {0}. Published Document ID: {1}. Duration: {2} seconds.", CREATE_PDF_THUMBNAIL_ACTION,
                        publishedDocumentId, duration);
                logger.info(logEnd);

            } catch (Exception e) {
                jmsErrorHandler.handleDocumentPublishError(CREATE_PDF_THUMBNAIL_ACTION, queue, publishedDocumentId, e);
            }
        } else {
            String log = MessageFormat.format(OsaConstantUtil.NO_ELEMENT_IN_QUEUE_LOG_MESSAGE, queue);
            logger.info(log);
        }
    }

}
