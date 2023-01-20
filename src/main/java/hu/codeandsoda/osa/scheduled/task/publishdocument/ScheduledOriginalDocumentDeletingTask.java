package hu.codeandsoda.osa.scheduled.task.publishdocument;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import hu.codeandsoda.osa.scheduled.service.ScheduledOriginalDocumentDeletingService;

@Component
public class ScheduledOriginalDocumentDeletingTask {

    private static Logger logger = LoggerFactory.getLogger(ScheduledOriginalDocumentDeletingTask.class);

    @Autowired
    private ScheduledOriginalDocumentDeletingService scheduledOriginalDocumentDeletingService;

    @Scheduled(initialDelayString = "${scheduled-tasks.publish-documents.delete-original-document.initial-delay}", fixedDelayString = "${scheduled-tasks.publish-documents.delete-original-document.fixed-delay}")
    public void deleteOriginalDocument() {
        logger.info("START Scheduled Task - Publish document: Delete original document from database.");
        scheduledOriginalDocumentDeletingService.deleteOriginalDocument();
    }

}
