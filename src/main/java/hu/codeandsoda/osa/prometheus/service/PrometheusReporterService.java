package hu.codeandsoda.osa.prometheus.service;

import java.text.MessageFormat;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.ObjectError;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;

@Service
public class PrometheusReporterService {

    private static final String ERROR_TAG_NAME = "error";
    private static final String PUBLISHED_DOCUMENT_ID_TAG_NAME = "publishedDocumentId";
    private static final String DOCUMENT_ID_TAG_NAME = "documentId";
    private static final String QUEUE_TAG_NAME = "queue";

    private static final String EMAIL_SENDING_ERROR_COUNTER_NAME = "email_sending_error";
    private static final String CONFIRM_REGISTRATION_ERROR_COUNTER_NAME = "confirm_registration_error";
    private static final String GENERATE_THUMBNAIL_ERROR_COUNTER_NAME = "generate_thumbnail_error";
    private static final String UPLOAD_MEDIA_TO_S3_ERROR_COUNTER_NAME = "upload_media_to_s3_error";
    private static final String COPY_MEDIA_TO_S3_ERROR_COUNTER_NAME = "copy_media_to_s3_error";
    private static final String LOAD_MEDIA_FROM_S3_ERROR_COUNTER_NAME = "load_media_from_s3_error";
    private static final String DELETE_IMAGE_ERROR_COUNTER_NAME = "delete_image_error";
    private static final String GENERATE_OCR_ERROR_COUNTER_NAME = "generate_ocr_error";
    private static final String GENERATE_PDF_ERROR_COUNTER_NAME = "generate_pdf_error";
    private static final String UPLOAD_PDF_TO_IPFS_ERROR_COUNTER_NAME = "upload_pdf_to_ipfs_error";

    private static final String OCR_REQUEST_DURATION_SECONDS_COUNTER_NAME = "ocr_request_duration_seconds";
    private static final String OCR_REQUEST_IMAGE_NUMBER_COUNTER_NAME = "ocr_request_image_number";
    private static final String OCR_REQUEST_IMAGES_TOTAL_SIZE_COUNTER_NAME = "ocr_request_images_total_size";

    private static final String CREATE_IN_PROGRESS_PUBLISHED_DOCUMENT_COUNTER_NAME = "create_in_progress_published_document";
    private static final String SET_PUBLISHED_DOCUMENT_STATUS_TO_PUBLISHED_COUNTER_NAME = "set_published_document_status_to_published";
    private static final String GENERATE_OCR_QUEUE_ERROR_COUNTER_NAME = "generate_ocr_queue_error";
    private static final String PUBLISH_DOCUMENT_QUEUE_ERROR_COUNTER_NAME = "publish_document_queue_error";

    @Autowired
    private MeterRegistry meterRegistry;

    private Counter emailSendingErrorCounter;

    private Counter confirmRegistrationErrorCounter;

    private Counter generateThumbnailErrorCounter;

    private Counter uploadMediaToS3ErrorCounter;

    private Counter copyMediaToS3ErrorCounter;

    private Counter loadMediaFromS3ErrorCounter;

    private Counter deleteImageErrorCounter;

    private Counter generateOcrErrorCounter;

    private Counter generatePdfErrorCounter;

    private Counter uploadPdfToIpfsErrorCounter;

    private Counter ocrRequestDurationSecondsCounter;

    private Counter ocrRequestImageNumberCounter;

    private Counter ocrRequestImagesTotalSizeCounter;

    private Counter createInProgressPublishedDocumentCounter;

    private Counter setPublishedDocumentStatusToPublishedCounter;

    private Counter generateOcrQueueErrorCounter;

    private Counter publishDocumentQueueErrorCounter;

    public void sendEmailError(String error) {
        emailSendingErrorCounter = meterRegistry.counter(EMAIL_SENDING_ERROR_COUNTER_NAME, ERROR_TAG_NAME, error);
        emailSendingErrorCounter.increment();
    }

    public void sendConfirmRegistrationError(String error) {
        confirmRegistrationErrorCounter = meterRegistry.counter(CONFIRM_REGISTRATION_ERROR_COUNTER_NAME, ERROR_TAG_NAME, error);
        confirmRegistrationErrorCounter.increment();
    }

    public void sendGenerateThumbnailError(String error) {
        generateThumbnailErrorCounter = meterRegistry.counter(GENERATE_THUMBNAIL_ERROR_COUNTER_NAME, ERROR_TAG_NAME, error);
        generateThumbnailErrorCounter.increment();
    }

    public void sendUploadMediaToS3Error(String error) {
        uploadMediaToS3ErrorCounter = meterRegistry.counter(UPLOAD_MEDIA_TO_S3_ERROR_COUNTER_NAME, ERROR_TAG_NAME, error);
        uploadMediaToS3ErrorCounter.increment();
    }

    public void sendCopyMediaToS3Error(String error) {
        copyMediaToS3ErrorCounter = meterRegistry.counter(COPY_MEDIA_TO_S3_ERROR_COUNTER_NAME, ERROR_TAG_NAME, error);
        copyMediaToS3ErrorCounter.increment();
    }

    public void sendLoadMediaFromS3Error(String error) {
        loadMediaFromS3ErrorCounter = meterRegistry.counter(LOAD_MEDIA_FROM_S3_ERROR_COUNTER_NAME, ERROR_TAG_NAME, error);
        loadMediaFromS3ErrorCounter.increment();
    }

    public void sendDeleteImageError(String error) {
        deleteImageErrorCounter = meterRegistry.counter(DELETE_IMAGE_ERROR_COUNTER_NAME, ERROR_TAG_NAME, error);
        deleteImageErrorCounter.increment();
    }

    public void sendGenerateOcrError(String error) {
        generateOcrErrorCounter = meterRegistry.counter(GENERATE_OCR_ERROR_COUNTER_NAME, ERROR_TAG_NAME, error);
        generateOcrErrorCounter.increment();
    }

    public void sendGenerateOcrErrorWithErrors(List<ObjectError> errors) {
        Tags tags = loadOcrErrorTags(errors);
        generateOcrErrorCounter = meterRegistry.counter(GENERATE_OCR_ERROR_COUNTER_NAME, tags);
        generateOcrErrorCounter.increment();
    }

    public void sendGeneratePdfError(String error) {
        generatePdfErrorCounter = meterRegistry.counter(GENERATE_PDF_ERROR_COUNTER_NAME, ERROR_TAG_NAME, error);
        generatePdfErrorCounter.increment();
    }

    public void sendUploadPdfToIpfsError(String error) {
        uploadPdfToIpfsErrorCounter = meterRegistry.counter(UPLOAD_PDF_TO_IPFS_ERROR_COUNTER_NAME, ERROR_TAG_NAME, error);
        uploadPdfToIpfsErrorCounter.increment();
    }

    public void updateOcrRequestDurationSecondsCounter(Long documentId, long ocrRequestDurationSeconds) {
        Tags tags = loadDocumentTags(documentId);
        ocrRequestDurationSecondsCounter = meterRegistry.counter(OCR_REQUEST_DURATION_SECONDS_COUNTER_NAME, tags);
        ocrRequestDurationSecondsCounter.increment(ocrRequestDurationSeconds);
    }

    public void updateOcrRequestImageNumberCounter(Long documentId, int ocrRequestImageNumber) {
        Tags tags = loadDocumentTags(documentId);
        ocrRequestImageNumberCounter = meterRegistry.counter(OCR_REQUEST_IMAGE_NUMBER_COUNTER_NAME, tags);
        ocrRequestImageNumberCounter.increment(ocrRequestImageNumber);
    }

    public void updateOcrRequestImagesTotalSizeCounter(Long documentId, int ocrRequestImagesTotalSize) {
        Tags tags = loadDocumentTags(documentId);
        ocrRequestImagesTotalSizeCounter = meterRegistry.counter(OCR_REQUEST_IMAGES_TOTAL_SIZE_COUNTER_NAME, tags);
        ocrRequestImagesTotalSizeCounter.increment(ocrRequestImagesTotalSize);
    }

    public void updateCreateInProgressPublishedDocumentCounter(Long publishedDocumentId) {
        createInProgressPublishedDocumentCounter = meterRegistry.counter(CREATE_IN_PROGRESS_PUBLISHED_DOCUMENT_COUNTER_NAME, PUBLISHED_DOCUMENT_ID_TAG_NAME,
                publishedDocumentId.toString());
        createInProgressPublishedDocumentCounter.increment();
    }

    public void updateSetPublishedDocumentStatusToPublishedCounter(Long publishedDocumentId) {
        setPublishedDocumentStatusToPublishedCounter = meterRegistry.counter(SET_PUBLISHED_DOCUMENT_STATUS_TO_PUBLISHED_COUNTER_NAME, PUBLISHED_DOCUMENT_ID_TAG_NAME,
                publishedDocumentId.toString());
        setPublishedDocumentStatusToPublishedCounter.increment();
    }

    public void sendGenerateOcrQueueErrorCounter(Long documentId, String queue, String error) {
        generateOcrQueueErrorCounter = meterRegistry.counter(GENERATE_OCR_QUEUE_ERROR_COUNTER_NAME, DOCUMENT_ID_TAG_NAME, documentId.toString(),
                QUEUE_TAG_NAME, queue, ERROR_TAG_NAME, error);
        generateOcrQueueErrorCounter.increment();
    }

    public void sendPublishDocumentQueueErrorCounter(Long publishedDocumentId, String queue, String error) {
        publishDocumentQueueErrorCounter = meterRegistry.counter(PUBLISH_DOCUMENT_QUEUE_ERROR_COUNTER_NAME, PUBLISHED_DOCUMENT_ID_TAG_NAME, publishedDocumentId.toString(),
                QUEUE_TAG_NAME, queue, ERROR_TAG_NAME, error);
        publishDocumentQueueErrorCounter.increment();
    }

    private Tags loadOcrErrorTags(List<ObjectError> errors) {
        Tags tags = Tags.empty();
        int index = 0;
        for (ObjectError error : errors) {
            String key = MessageFormat.format("error-{0}", index);
            tags.and(key, error.getDefaultMessage());

            index++;
        }
        return tags;
    }

    private Tags loadDocumentTags(Long documentId) {
        Tags tags = Tags.of(DOCUMENT_ID_TAG_NAME, documentId.toString());
        return tags;
    }

}
