package hu.codeandsoda.osa.jms.service.documentpublish;

import java.text.MessageFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import hu.codeandsoda.osa.jms.config.ActiveMQConfig;

@Service
public class PublishDocumentMessageService {

    private static Logger logger = LoggerFactory.getLogger(PublishDocumentMessageService.class);

    @Autowired
    private JmsTemplate jmsTemplate;

    public void addToQueue(Long publishedDocumentId) {
        final String queue = ActiveMQConfig.PUBLISH_DOCUMENT_QUEUE;

        jmsTemplate.convertAndSend(queue, publishedDocumentId);

        String logMessage = MessageFormat.format("action=addToQueue, status=success, queue={0}, message=publishedDocumentId, publishedDocumentId={1}", queue, publishedDocumentId);
        logger.info(logMessage);
    }

}
