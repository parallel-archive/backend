package hu.codeandsoda.osa.jms.service.email;

import java.text.MessageFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import hu.codeandsoda.osa.email.data.EmailParams;
import hu.codeandsoda.osa.jms.config.ActiveMQConfig;

@Service
public class EmailMessageService {

    private static Logger logger = LoggerFactory.getLogger(EmailMessageService.class);

    @Autowired
    private JmsTemplate jmsTemplate;

    public void send(EmailParams emailParams) {
        jmsTemplate.convertAndSend(ActiveMQConfig.EMAIL_QUEUE, emailParams);

        String logMessage = MessageFormat.format("action=addToQueue, status=success, queue={0}, message=emailParams, userId={1}, emailSubject={2}", ActiveMQConfig.EMAIL_QUEUE,
                emailParams.getUserId(), emailParams.getSubject());
        logger.info(logMessage);
    }

}
