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

import hu.codeandsoda.osa.document.domain.Document;
import hu.codeandsoda.osa.document.service.DocumentService;
import hu.codeandsoda.osa.documentpublish.domain.PublishedDocument;
import hu.codeandsoda.osa.documentpublish.service.PublishedDocumentService;
import hu.codeandsoda.osa.jms.config.ActiveMQConfig;
import hu.codeandsoda.osa.jms.service.documentpublish.PublishDocumentIndexMessageService;
import hu.codeandsoda.osa.jms.service.error.JmsErrorHandler;
import hu.codeandsoda.osa.util.OsaConstantUtil;

@Service
public class ScheduledOriginalDocumentDeletingService {

    private static final String DELETE_ORIGINAL_DOCUMENT_ACTION = "Delete original document from database";

    private static Logger logger = LoggerFactory.getLogger(ScheduledOriginalDocumentDeletingService.class);

    @Autowired
    private JmsTemplate jmsTemplate;

    @Autowired
    private PublishedDocumentService publishedDocumentService;

    @Autowired
    private DocumentService documentService;

    @Autowired
    private PublishDocumentIndexMessageService publishDocumentIndexMessageService;

    @Autowired
    private JmsErrorHandler jmsErrorHandler;

    @Async("asyncDocumentPublishExecutor")
    public void deleteOriginalDocument() {
        final String queue = ActiveMQConfig.PUBLISH_DOCUMENT_DELETE_ORIGINAL_DOCUMENT_QUEUE;
        Long publishedDocumentId = (Long) jmsTemplate.receiveAndConvert(queue);

        if (null != publishedDocumentId) {
            PublishedDocument publishedDocument = publishedDocumentService.loadById(publishedDocumentId);

            Document document = documentService.loadByPublishedDocument(publishedDocument);
            Long documentId = document.getId();

            ZonedDateTime start = ZonedDateTime.now();
            String logStart = MessageFormat.format(
                    "START Publish document: {0}. Publish Document ID: {1}. Original document ID : {2}. Current time is: {3}", DELETE_ORIGINAL_DOCUMENT_ACTION,
                    publishedDocumentId, documentId, start);
            logger.info(logStart);

            Errors errors = new BeanPropertyBindingResult(publishedDocumentId, "publishedDocumentId");
            try {
                publishedDocumentService.deleteOriginalDocument(document, errors);

                publishDocumentIndexMessageService.addToQueue(publishedDocumentId);

                long duration = Duration.between(start, ZonedDateTime.now()).getSeconds();
                String logEnd = MessageFormat.format(
                        "END Publish document: {0}. Published Document ID: {1}. Original document ID : {2}. Duration: {3} seconds.", DELETE_ORIGINAL_DOCUMENT_ACTION,
                        publishedDocumentId, documentId, duration);
                logger.info(logEnd);

            } catch (Exception e) {
                jmsErrorHandler.handleDocumentPublishError(DELETE_ORIGINAL_DOCUMENT_ACTION, queue, publishedDocumentId, e);
            }
        } else {
            String log = MessageFormat.format(OsaConstantUtil.NO_ELEMENT_IN_QUEUE_LOG_MESSAGE, queue);
            logger.info(log);
        }
    }

}
