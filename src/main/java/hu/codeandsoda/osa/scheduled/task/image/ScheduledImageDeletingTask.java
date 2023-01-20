package hu.codeandsoda.osa.scheduled.task.image;

import java.text.MessageFormat;
import java.util.Calendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import hu.codeandsoda.osa.jms.config.ActiveMQConfig;
import hu.codeandsoda.osa.media.service.MediaService;
import hu.codeandsoda.osa.myshoebox.data.DeleteImagesData;
import hu.codeandsoda.osa.prometheus.service.PrometheusReporterService;
import hu.codeandsoda.osa.util.OsaConstantUtil;

@Component
public class ScheduledImageDeletingTask {

    private static Logger logger = LoggerFactory.getLogger(ScheduledImageDeletingTask.class);

    @Autowired
    private JmsTemplate jmsTemplate;

    @Autowired
    private MediaService mediaService;

    @Autowired
    private PrometheusReporterService prometheusReporterService;

    @Scheduled(initialDelayString = "${scheduled-tasks.delete-images.initial-delay}", fixedDelayString = "${scheduled-tasks.delete-images.fixed-delay}")
    public void deleteImages() {
        final String queue = ActiveMQConfig.DELETE_IMAGE_QUEUE;
        DeleteImagesData deleteImagesData = (DeleteImagesData) jmsTemplate.receiveAndConvert(queue);

        if (null != deleteImagesData) {
            String logStart = MessageFormat.format("STARTED Delete Images scheduled task. Current time is :: {0}", Calendar.getInstance().getTime());
            logger.info(logStart);

            try {
                int imagesSize = deleteImagesData.getUrls().size();

                if (imagesSize > 0) {
                    String logMessage = MessageFormat.format("Scheduled Image delete : size={0}", imagesSize);
                    logger.info(logMessage);
                    mediaService.deleteImageUrls(deleteImagesData);
                } else {
                    String logMessage = MessageFormat.format("Scheduled Image delete : size={0}. No images to delete.", imagesSize);
                    logger.info(logMessage);
                }

                String logEnd = MessageFormat.format("FINISHED Delete Images scheduled task. Current time is :: {0}", Calendar.getInstance().getTime());
                logger.info(logEnd);
            } catch (Exception e) {
                String errorLog = MessageFormat.format("action=deleteImages, status=failed, errorMessage={0}", e.getMessage());
                logger.error(errorLog, e);

                prometheusReporterService.sendDeleteImageError(e.getMessage());
            }
        } else {
            String log = MessageFormat.format(OsaConstantUtil.NO_ELEMENT_IN_QUEUE_LOG_MESSAGE, queue);
            logger.info(log);
        }
    }

}
