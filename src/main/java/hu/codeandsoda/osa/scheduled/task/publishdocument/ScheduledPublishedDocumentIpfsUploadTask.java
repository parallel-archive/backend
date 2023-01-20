package hu.codeandsoda.osa.scheduled.task.publishdocument;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import hu.codeandsoda.osa.scheduled.service.ScheduledPublishedDocumentIpfsUploadService;

@Component
public class ScheduledPublishedDocumentIpfsUploadTask {

    private static Logger logger = LoggerFactory.getLogger(ScheduledPublishedDocumentIpfsUploadTask.class);

    @Autowired
    private ScheduledPublishedDocumentIpfsUploadService scheduledPublishedDocumentIpfsUploadService;

    @Scheduled(initialDelayString = "${scheduled-tasks.publish-documents.upload-to-ipfs.initial-delay}", fixedDelayString = "${scheduled-tasks.publish-documents.upload-to-ipfs.fixed-delay}")
    public void uploadPdfToIpfs() {
        logger.info("START Scheduled Task - Publish document: upload PDF to IPFS.");
        scheduledPublishedDocumentIpfsUploadService.uploadPdfToIpfs();
    }



}
