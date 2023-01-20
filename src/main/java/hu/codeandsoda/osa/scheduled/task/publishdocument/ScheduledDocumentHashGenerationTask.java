package hu.codeandsoda.osa.scheduled.task.publishdocument;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import hu.codeandsoda.osa.scheduled.service.ScheduledDocumentHashGenerationService;

@Component
public class ScheduledDocumentHashGenerationTask {

    private static Logger logger = LoggerFactory.getLogger(ScheduledDocumentHashGenerationTask.class);

    @Autowired
    private ScheduledDocumentHashGenerationService scheduledDocumentHashGenerationService;

    @Scheduled(initialDelayString = "${scheduled-tasks.publish-documents.generate-hash.initial-delay}", fixedDelayString = "${scheduled-tasks.publish-documents.generate-hash.fixed-delay}")
    public void generateDocumentHash() {
        logger.info("START Scheduled Task - Publish document: Generate document hash.");
        scheduledDocumentHashGenerationService.generateDocumentHash();
    }

}
