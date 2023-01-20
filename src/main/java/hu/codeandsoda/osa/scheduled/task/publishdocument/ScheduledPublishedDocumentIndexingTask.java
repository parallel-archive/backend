package hu.codeandsoda.osa.scheduled.task.publishdocument;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import hu.codeandsoda.osa.scheduled.service.ScheduledPublishedDocumentIndexingService;

@Component
public class ScheduledPublishedDocumentIndexingTask {

    private static Logger logger = LoggerFactory.getLogger(ScheduledPublishedDocumentIndexingTask.class);

    @Autowired
    private ScheduledPublishedDocumentIndexingService scheduledPublishedDocumentIndexingService;

    @Scheduled(initialDelayString = "${scheduled-tasks.publish-documents.index-published-document.initial-delay}", fixedDelayString = "${scheduled-tasks.publish-documents.index-published-document.fixed-delay}")
    public void indexPublishedDocument() {
        logger.info("START Scheduled Task - Publish document: Index Published Document.");
        scheduledPublishedDocumentIndexingService.indexPublishedDocument();
    }
}
