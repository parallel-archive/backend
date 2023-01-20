package hu.codeandsoda.osa.scheduled.task.publishdocument;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import hu.codeandsoda.osa.scheduled.service.ScheduledPdfThumbnailCreationService;

@Component
public class ScheduledPdfThumbnailCreationTask {

    private static Logger logger = LoggerFactory.getLogger(ScheduledPdfThumbnailCreationTask.class);

    @Autowired
    private ScheduledPdfThumbnailCreationService scheduledPdfThumbnailCreationService;

    @Scheduled(initialDelayString = "${scheduled-tasks.publish-documents.create-pdf-thumbnail.initial-delay}", fixedDelayString = "${scheduled-tasks.publish-documents.create-pdf-thumbnail.fixed-delay}")
    public void createPdfThumbnail() {
        logger.info("START Scheduled Task - Publish document: Create and upload PDF thumbnail.");
        scheduledPdfThumbnailCreationService.createPdfThumbnail();
    }

}
