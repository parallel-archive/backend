package hu.codeandsoda.osa.email.service;

import java.io.UnsupportedEncodingException;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import hu.codeandsoda.osa.email.data.EmailParams;
import hu.codeandsoda.osa.prometheus.service.PrometheusReporterService;

@Service
public class EmailService {

    private static Logger logger = LoggerFactory.getLogger(EmailService.class);

    @Value("${osa.email.from}")
    private String fromEmail;

    @Value("${osa.email.name}")
    private String emailName;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private PrometheusReporterService prometheusReporterService;

    @Async("asyncEmailExecutor")
    public void send(EmailParams emailParams) {
        MimeMessage message = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(emailParams.getTo());
            helper.setFrom(fromEmail, emailName);
            helper.setSubject(emailParams.getSubject());
            helper.setText(emailParams.getTextMessage(), emailParams.getHtmlMessage());
            mailSender.send(message);

            String logMessage = new StringBuilder().append("action=sendEmail, status= success, userId=").append(emailParams.getUserId()).append(", emailSubject=")
                    .append(emailParams.getSubject()).toString();
            logger.info(logMessage);
        } catch (MessagingException | UnsupportedEncodingException e) {
            String logMessage = new StringBuilder().append("action=sendEmail, status= error, userId=").append(emailParams.getUserId()).append(", emailSubject=")
                    .append(emailParams.getSubject()).append(", error=").append(e.getMessage()).toString();
            logger.error(logMessage);
            prometheusReporterService.sendEmailError(e.getMessage());
        }
    }

}
