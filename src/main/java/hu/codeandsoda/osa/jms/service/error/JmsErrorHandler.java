package hu.codeandsoda.osa.jms.service.error;

import java.text.MessageFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import hu.codeandsoda.osa.prometheus.service.PrometheusReporterService;

@Service
public class JmsErrorHandler {

    private static Logger logger = LoggerFactory.getLogger(JmsErrorHandler.class);

    @Autowired
    private PrometheusReporterService prometheusReporterService;

    @Autowired
    private JmsTemplate jmsTemplate;

    public void handleDocumentPublishError(String action, String queue, Long publishedDocumentId, Exception exception) {
        String exceptionMessage = exception.getMessage();
        String errorLog = MessageFormat.format("ERROR Publish document, action={0}, publishedDocumentId={1}, requeue-to={2}, error={3}", action, publishedDocumentId, queue,
                exceptionMessage);
        logger.error(errorLog, exception);
        
        prometheusReporterService.sendPublishDocumentQueueErrorCounter(publishedDocumentId, queue, exceptionMessage);

        jmsTemplate.convertAndSend(queue, publishedDocumentId);
    }

}
