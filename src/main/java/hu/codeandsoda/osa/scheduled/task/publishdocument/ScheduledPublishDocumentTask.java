package hu.codeandsoda.osa.scheduled.task.publishdocument;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import hu.codeandsoda.osa.scheduled.service.ScheduledPublishDocumentService;

@Component
public class ScheduledPublishDocumentTask {

    private static Logger logger = LoggerFactory.getLogger(ScheduledPublishDocumentTask.class);

    @Autowired
    private ScheduledPublishDocumentService scheduledPublishDocumentService;

    @Scheduled(initialDelayString = "${scheduled-tasks.publish-documents.publish-document.initial-delay}", fixedDelayString = "${scheduled-tasks.publish-documents.publish-document.fixed-delay}")
    public void publishDocument() {
        logger.info("START Scheduled Task - Publish document: Validate and set Document status to Published.");
        scheduledPublishDocumentService.publishDocument();
    }


}
