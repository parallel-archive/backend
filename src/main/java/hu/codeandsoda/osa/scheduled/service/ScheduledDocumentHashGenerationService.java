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
import hu.codeandsoda.osa.jms.service.documentpublish.PublishDocumentGeneratePdfMessageService;
import hu.codeandsoda.osa.jms.service.error.JmsErrorHandler;
import hu.codeandsoda.osa.util.OsaConstantUtil;

@Service
public class ScheduledDocumentHashGenerationService {

    private static final String GENERATE_HASH_ACTION = "Generate document hash";

    private static Logger logger = LoggerFactory.getLogger(ScheduledDocumentHashGenerationService.class);

    @Autowired
    private JmsTemplate jmsTemplate;

    @Autowired
    private PublishedDocumentService publishedDocumentService;

    @Autowired
    private PublishDocumentGeneratePdfMessageService publishDocumentGeneratePdfMessageService;

    @Autowired
    private JmsErrorHandler jmsErrorHandler;

    @Async("asyncDocumentPublishExecutor")
    public void generateDocumentHash() {
        final String queue = ActiveMQConfig.PUBLISH_DOCUMENT_GENERATE_HASH_QUEUE;
        Long publishedDocumentId = (Long) jmsTemplate.receiveAndConvert(queue);

        if (null != publishedDocumentId) {
            ZonedDateTime start = ZonedDateTime.now();
            String logStart = MessageFormat.format("START Publish document: {0}. Publish Document ID: {1}. Current time is: {2}", GENERATE_HASH_ACTION, publishedDocumentId, start);
            logger.info(logStart);

            Errors errors = new BeanPropertyBindingResult(publishedDocumentId, "publishedDocumentId");
            try {
                publishedDocumentService.generateDocumentHash(publishedDocumentId, errors);

                publishDocumentGeneratePdfMessageService.addToQueue(publishedDocumentId);

                long duration = Duration.between(start, ZonedDateTime.now()).getSeconds();
                String logEnd = MessageFormat.format("END Publish document: {0}. Published Document ID: {1}. Duration: {2} seconds.", GENERATE_HASH_ACTION, publishedDocumentId,
                        duration);
                logger.info(logEnd);

            } catch (Exception e) {
                jmsErrorHandler.handleDocumentPublishError(GENERATE_HASH_ACTION, queue, publishedDocumentId, e);
            }
        } else {
            String log = MessageFormat.format(OsaConstantUtil.NO_ELEMENT_IN_QUEUE_LOG_MESSAGE, queue);
            logger.info(log);
        }
    }

}
