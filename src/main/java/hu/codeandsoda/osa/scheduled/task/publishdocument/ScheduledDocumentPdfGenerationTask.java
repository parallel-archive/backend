package hu.codeandsoda.osa.scheduled.task.publishdocument;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import hu.codeandsoda.osa.scheduled.service.ScheduledDocumentPdfGenerationService;

@Component
public class ScheduledDocumentPdfGenerationTask {

    private static Logger logger = LoggerFactory.getLogger(ScheduledDocumentPdfGenerationTask.class);

    @Autowired
    private ScheduledDocumentPdfGenerationService scheduledDocumentPdfGenerationService;

    @Scheduled(initialDelayString = "${scheduled-tasks.publish-documents.generate-pdf.initial-delay}", fixedDelayString = "${scheduled-tasks.publish-documents.generate-pdf.fixed-delay}")
    public void generatePublishedDocumentPdf() {
        logger.info("START Scheduled Task - Publish document: Generate PDF from document and upload.");
        scheduledDocumentPdfGenerationService.generatePublishedDocumentPdf();
    }

}
