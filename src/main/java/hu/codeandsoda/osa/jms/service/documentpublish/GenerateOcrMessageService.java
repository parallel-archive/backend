package hu.codeandsoda.osa.jms.service.documentpublish;

import java.text.MessageFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import hu.codeandsoda.osa.document.data.GenerateOcrRequestData;
import hu.codeandsoda.osa.jms.config.ActiveMQConfig;

@Service
public class GenerateOcrMessageService {

    private static Logger logger = LoggerFactory.getLogger(GenerateOcrMessageService.class);

    @Autowired
    private JmsTemplate jmsTemplate;

    public void addToQueue(GenerateOcrRequestData generateOcrRequestData) {
        final String queue = ActiveMQConfig.GENERATE_OCR_QUEUE;

        jmsTemplate.convertAndSend(queue, generateOcrRequestData);

        Long documentId = generateOcrRequestData.getDocumentId();
        String logMessage = MessageFormat.format("action=addToQueue, status=success, queue={0}, message=generateOcrRequestData, documentId={1}", queue, documentId);
        logger.info(logMessage);
    }

}
