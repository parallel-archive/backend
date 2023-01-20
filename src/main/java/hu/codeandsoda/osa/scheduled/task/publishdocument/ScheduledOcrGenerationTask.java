package hu.codeandsoda.osa.scheduled.task.publishdocument;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import hu.codeandsoda.osa.scheduled.service.ScheduledOcrGenerationService;

@Component
public class ScheduledOcrGenerationTask {

    private static Logger logger = LoggerFactory.getLogger(ScheduledOcrGenerationTask.class);

    @Autowired
    private ScheduledOcrGenerationService scheduledOcrGenerationService;

    @Scheduled(initialDelayString = "${scheduled-tasks.publish-documents.generate-ocr.initial-delay}", fixedDelayString = "${scheduled-tasks.publish-documents.generate-ocr.fixed-delay}")
    public void generateOcr() {
        logger.info("START Scheduled Task - Generate OCR from document images.");
        scheduledOcrGenerationService.generateOcr();
    }



}
