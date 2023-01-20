package hu.codeandsoda.osa.scheduled.task.email;

import java.text.MessageFormat;
import java.util.Calendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import hu.codeandsoda.osa.email.data.EmailParams;
import hu.codeandsoda.osa.email.service.EmailService;
import hu.codeandsoda.osa.jms.config.ActiveMQConfig;
import hu.codeandsoda.osa.util.OsaConstantUtil;

@Component
public class ScheduledEmailSendingTask {

    private static Logger logger = LoggerFactory.getLogger(ScheduledEmailSendingTask.class);

    @Autowired
    private EmailService emailService;

    @Autowired
    private JmsTemplate jmsTemplate;

    @Scheduled(initialDelayString = "${scheduled-tasks.send-emails.initial-delay}", fixedDelayString = "${scheduled-tasks.send-emails.fixed-delay}")
    public void sendEmails() {
        final String queue = ActiveMQConfig.EMAIL_QUEUE;
        EmailParams emailParams = (EmailParams) jmsTemplate.receiveAndConvert(queue);

        if (null != emailParams) {
            String logStart = MessageFormat.format("STARTED E-mail sender scheduled task. Current time is :: {0}", Calendar.getInstance().getTime());
            logger.info(logStart);

            String logMessage = MessageFormat.format("Scheduled e-mail : userId={0}, emailSubject={1}", emailParams.getUserId(), emailParams.getSubject());
            logger.info(logMessage);

            emailService.send(emailParams);

            String logEnd = new StringBuilder().append("FINISHED E-mail sender scheduled task. Current time is :: ").append(Calendar.getInstance().getTime()).toString();
            logger.info(logEnd);
        } else {
            String log = MessageFormat.format(OsaConstantUtil.NO_ELEMENT_IN_QUEUE_LOG_MESSAGE, queue);
            logger.info(log);
        }
    }

}
