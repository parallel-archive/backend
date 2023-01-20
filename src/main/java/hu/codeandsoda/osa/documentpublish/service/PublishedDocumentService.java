package hu.codeandsoda.osa.documentpublish.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.transaction.Transactional;
import javax.xml.bind.DatatypeConverter;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.SerializationUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;

import hu.codeandsoda.osa.account.user.data.UserPublishedDocumentAnnotationData;
import hu.codeandsoda.osa.account.user.domain.User;
import hu.codeandsoda.osa.document.data.GenerateOcrRequestData;
import hu.codeandsoda.osa.document.domain.Document;
import hu.codeandsoda.osa.document.domain.DocumentImage;
import hu.codeandsoda.osa.document.domain.DocumentTag;
import hu.codeandsoda.osa.document.service.DocumentService;
import hu.codeandsoda.osa.document.validator.PublishedDocumentValidator;
import hu.codeandsoda.osa.documentfilter.data.DocumentFilterTypeName;
import hu.codeandsoda.osa.documentfilter.data.DocumentFilterTypesData;
import hu.codeandsoda.osa.documentfilter.data.PeriodFilterData;
import hu.codeandsoda.osa.documentfilter.domain.DocumentFilter;
import hu.codeandsoda.osa.documentfilter.service.DocumentFilterTypeService;
import hu.codeandsoda.osa.documentpublish.data.DocumentPublishResponseData;
import hu.codeandsoda.osa.documentpublish.data.DocumentPublishResult;
import hu.codeandsoda.osa.documentpublish.data.PublicArchivePageData;
import hu.codeandsoda.osa.documentpublish.data.PublicArchivePageFilteringRequest;
import hu.codeandsoda.osa.documentpublish.data.PublishedDocumentData;
import hu.codeandsoda.osa.documentpublish.data.PublishedDocumentStatus;
import hu.codeandsoda.osa.documentpublish.data.PublishedDocumentsData;
import hu.codeandsoda.osa.documentpublish.domain.PublishedDocument;
import hu.codeandsoda.osa.documentpublish.domain.PublishedDocumentAnnotation;
import hu.codeandsoda.osa.documentpublish.repository.PublishedDocumentRepository;
import hu.codeandsoda.osa.email.data.EmailParams;
import hu.codeandsoda.osa.email.data.EmailTemplate;
import hu.codeandsoda.osa.exception.ValidationException;
import hu.codeandsoda.osa.general.data.ResponseMessage;
import hu.codeandsoda.osa.ipfs.exception.IpfsUploadException;
import hu.codeandsoda.osa.ipfs.service.IpfsService;
import hu.codeandsoda.osa.jms.service.documentpublish.GenerateOcrMessageService;
import hu.codeandsoda.osa.jms.service.email.EmailMessageService;
import hu.codeandsoda.osa.media.exception.MediaCopyException;
import hu.codeandsoda.osa.media.exception.MediaLoadingException;
import hu.codeandsoda.osa.media.exception.MediaUploadException;
import hu.codeandsoda.osa.media.service.MediaService;
import hu.codeandsoda.osa.ocr.service.OcrService;
import hu.codeandsoda.osa.pagination.data.PaginationRequest;
import hu.codeandsoda.osa.pagination.service.PaginationService;
import hu.codeandsoda.osa.pdf.exception.CreatePDFException;
import hu.codeandsoda.osa.pdf.service.PdfService;
import hu.codeandsoda.osa.prometheus.service.PrometheusReporterService;
import hu.codeandsoda.osa.search.service.PublishedDocumentMetaDataIndexService;
import hu.codeandsoda.osa.sort.data.SortingRequest;
import hu.codeandsoda.osa.sort.service.SortPublishedDocumentsService;
import hu.codeandsoda.osa.util.ErrorCode;

@Service
public class PublishedDocumentService {

    private static final String PUBLISHED_DOCUMENT_S3_KEY = "published_documents/{0}.pdf";
    private static final String PUBLISHED_DOCUMENT_THUMBNAIL_S3_KEY = "published_documents/thumbnail_{0}";

    private static Logger logger = LoggerFactory.getLogger(PublishedDocumentService.class);

    @Autowired
    private PdfService pdfService;

    @Autowired
    private MediaService mediaService;

    @Autowired
    private DocumentService documentService;

    @Autowired
    private PublishedDocumentDataService publishedDocumentDataService;

    @Autowired
    private PaginationService paginationService;

    @Autowired
    private SortPublishedDocumentsService sortPublishedDocumentsService;

    @Autowired
    private DocumentFilterTypeService documentFilterTypeService;

    @Autowired
    private OcrService ocrService;

    @Autowired
    private PublishedDocumentMetaDataIndexService publishedDocumentMetaDataIndexService;

    @Autowired
    private IpfsService ipfsService;

    @Autowired
    private PublishedDocumentAnnotationService publishedDocumentAnnotationService;

    @Autowired
    private PrometheusReporterService prometheusReporterService;

    @Autowired
    private GenerateOcrMessageService generateOcrMessageService;

    @Autowired
    private PublishedDocumentValidator publishedDocumentValidator;

    @Autowired
    private EmailMessageService emailMessageService;

    @Autowired
    private PublishedDocumentRepository publishedDocumentRepository;

    @Value("${osa.baseUrl}")
    private String baseUrl;

    @Value("${osa.ocr.limit}")
    private int ocrLimit;

    @Value("${local.fastPublicArchivesQuery:false}")
    private boolean useFastPublicArchivesQuery;

    @Transactional
    public DocumentPublishResponseData addToPublishDocumentQueue(Document document, User user, Errors errors) {
        Long documentId = document.getId();

        Long publishedDocumentId = null;
        boolean ocrLimitReached = userOcrRateLimitReached(document, user);
        if (!ocrLimitReached) {
            PublishedDocument publishedDocument = constructWithoutPublishedDataAndSave(document, user);
            publishedDocumentId = publishedDocument.getId();
        }

        GenerateOcrRequestData generateOcrRequestData = new GenerateOcrRequestData.GenerateOcrRequestDataBuilder().setDocumentId(documentId)
                .setPublishedDocumentId(publishedDocumentId).build();
        generateOcrMessageService.addToQueue(generateOcrRequestData);

        DocumentPublishResult result = ocrLimitReached ? DocumentPublishResult.OCR_LIMIT_REACHED : DocumentPublishResult.PUBLISH_SCHEDULED;
        DocumentPublishResponseData response = new DocumentPublishResponseData.DocumentPublishResponseDataBuilder().setResult(result).build();
        return response;
    }

    public int countImagesWithoutOcr(Document document) {
        List<DocumentImage> documentImages = document.getImages();
        int imagesWithoutOcr = (int) documentImages.stream().filter(i -> !StringUtils.hasText(i.getOriginalOcr())).count();
        return imagesWithoutOcr;
    }

    @Transactional
    public void generateDocumentHash(Long publishedDocumentId, Errors errors) throws MediaUploadException {
        PublishedDocument publishedDocument = loadById(publishedDocumentId);
        Document document = documentService.loadByPublishedDocument(publishedDocument);

        try {
            byte[] data = SerializationUtils.serialize(document);
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedhash = digest.digest(data);
            String encodedHashAsString = DatatypeConverter.printHexBinary(encodedhash);

            publishedDocument.setHash(encodedHashAsString);
        } catch (NoSuchAlgorithmException e) {
            String logMessage = MessageFormat.format("action=publishDocument, status=error, publishDocumentId={0}, error={1}", publishedDocumentId, e.getMessage());
            logger.error(logMessage, e);
            errors.reject(ErrorCode.HASH_DOCUMENT_ERROR.toString(), "Could not generate document hash.");
            throw new MediaUploadException("Could not generate document hash.", errors.getAllErrors());
        }

        save(publishedDocument);
    }

    @Transactional
    public void generateAndUploadPdf(Long publishedDocumentId, Errors errors) throws MediaUploadException, CreatePDFException, MediaLoadingException {
        PublishedDocument publishedDocument = loadById(publishedDocumentId);
        Long userId = publishedDocument.getUser().getId();

        Document document = documentService.loadByPublishedDocument(publishedDocument);
        List<DocumentImage> documentImages = document.getImages();

        String pdfUrl = null;
        try (ByteArrayOutputStream os = pdfService.createPDFFromImages(publishedDocumentId, documentImages, errors)) {
            pdfUrl = uploadPdf(os, document, publishedDocument.getHash(), userId, errors);
            publishedDocument.setPdfUrl(pdfUrl);
        } catch (IOException e) {
            String logMessage = MessageFormat.format(
                    "action=savePublishedDocument, status=warning, error=Could not close pdf OutputStream resource after uploading to S3 bucket., documentId={0}, url={1}",
                    document.getId(), pdfUrl);
            logger.warn(logMessage, e);
        }

        save(publishedDocument);
    }

    @Transactional
    public void uploadPdfToIpfs(Long publishedDocumentId, Errors errors) throws MediaLoadingException, IpfsUploadException {
        PublishedDocument publishedDocument = loadById(publishedDocumentId);

        String ipfsContentId = ipfsService.upload(publishedDocument.getPdfUrl(), publishedDocument.getHash(), publishedDocumentId, errors);
        publishedDocument.setIpfsContentId(ipfsContentId);

        save(publishedDocument);
    }

    @Transactional
    public void uploadPdfThumbnail(Long publishedDocumentId, Errors errors) throws MediaCopyException {
        PublishedDocument publishedDocument = loadById(publishedDocumentId);
        Document document = documentService.loadByPublishedDocument(publishedDocument);

        String thumbnailUrl = uploadPdfThumbnail(document, publishedDocument.getHash(), errors);
        publishedDocument.setThumbnailUrl(thumbnailUrl);

        save(publishedDocument);
    }

    @Transactional
    public void deleteOriginalDocument(Document document, Errors errors) throws ValidationException {
        Long documentId = document.getId();
        Long userId = document.getUser().getId();

        documentService.deleteOriginalOfPublishedDocumentByIdAndUser(documentId, userId, errors);
    }

    public void indexPublishedDocument(Long publishedDocumentId) {
        PublishedDocument publishedDocument = loadById(publishedDocumentId);

        publishedDocumentMetaDataIndexService.index(publishedDocument);
    }

    @Transactional
    public void setStatusToPublished(Long publishedDocumentId, Errors errors) throws ValidationException {
        PublishedDocument publishedDocument = loadById(publishedDocumentId);

        publishedDocumentValidator.validate(publishedDocument, errors);
        if (errors.hasErrors()) {
            String message = MessageFormat.format("Invalid Published Document Fields. Published Document ID : {0}", publishedDocumentId);
            throw new ValidationException(message, errors.getAllErrors());
        }

        publishedDocument.setStatus(PublishedDocumentStatus.PUBLISHED);
        PublishedDocument savedPublishedDocument = save(publishedDocument);

        sendDocumentPublishCompletedEmail(savedPublishedDocument);

        prometheusReporterService.updateSetPublishedDocumentStatusToPublishedCounter(publishedDocumentId);
    }

    public void sendDocumentPublishCompletedEmail(PublishedDocument publishedDocument) {
        User user = publishedDocument.getUser();

        String publishedDocumentUrl = publishedDocumentDataService.constructPublishedDocumentUrl(publishedDocument.getHash());

        Map<String, String> replacements = new HashMap<>();
        replacements.put("user", user.getUsername());
        replacements.put("url", publishedDocumentUrl);

        EmailTemplate htmlTemplate = new EmailTemplate("document_publish_email.html");
        EmailTemplate textTemplate = new EmailTemplate("document_publish_email.txt");
        String htmlMessage = htmlTemplate.getTemplate(replacements);
        String textMessage = textTemplate.getTemplate(replacements);

        EmailParams emailParams = new EmailParams.EmailParamsBuilder().setTo(user.getEmail()).setSubject("Document Publish Completed").setHtmlMessage(htmlMessage)
                .setTextMessage(textMessage).setUserId(user.getId()).build();

        emailMessageService.send(emailParams);
    }

    public PublicArchivePageData loadPublicArchivePageData(PublicArchivePageFilteringRequest publicArchivePageFilteringRequest, SortingRequest sortingRequest,
            PaginationRequest paginationRequest, String searchTerm) {
        List<ResponseMessage> messages = new ArrayList<>();

        List<PublishedDocument> publishedDocuments = loadPublishedDocuments(publicArchivePageFilteringRequest, searchTerm);

        List<PublishedDocument> filteredPublishedDocuments = filterPublishedDocuments(publishedDocuments, publicArchivePageFilteringRequest);

        DocumentFilterTypesData documentFilterTypesData = documentFilterTypeService.loadFilterTypesData(publicArchivePageFilteringRequest, filteredPublishedDocuments);

        sortPublishedDocumentsService.sortPublishedDocuments(filteredPublishedDocuments, sortingRequest, messages);

        List<PublishedDocumentData> publishedDocumentDatas = publishedDocumentDataService.constructPublishedDocumentDatas(filteredPublishedDocuments);
        Page<PublishedDocumentData> publishedDocumentDataPage = paginationService.createPublishedDocumentDataPage(publishedDocumentDatas, paginationRequest, messages);

        PublishedDocumentsData publishedDocumentsData = new PublishedDocumentsData.PublishedDocumentsDataBuilder().setDocuments(publishedDocumentDataPage).setMessages(messages)
                .build();
        PublicArchivePageData publicArchivePageData = new PublicArchivePageData.PublicArchivePageDataBuilder().setPublishedDocuments(publishedDocumentsData)
                .setDocumentFilterTypesData(documentFilterTypesData).build();
        return publicArchivePageData;
    }

    public PublishedDocumentsData sortCollectionDocuments(List<PublishedDocument> collectionDocuments, SortingRequest sortingRequest, PaginationRequest paginationRequest) {
        List<ResponseMessage> messages = new ArrayList<>();

        sortPublishedDocumentsService.sortPublishedDocuments(collectionDocuments, sortingRequest, messages);

        List<PublishedDocumentData> publishedDocumentDatas = publishedDocumentDataService.constructPublishedDocumentDatas(collectionDocuments);
        Page<PublishedDocumentData> publishedDocumentDataPage = paginationService.createPublishedDocumentDataPage(publishedDocumentDatas, paginationRequest, messages);

        PublishedDocumentsData publishedDocumentsData = new PublishedDocumentsData.PublishedDocumentsDataBuilder().setDocuments(publishedDocumentDataPage).setMessages(messages)
                .build();
        return publishedDocumentsData;
    }

    public PublishedDocumentData loadPublishedDocument(String hash, User user) {
        PublishedDocument document = loadByHash(hash);
        updatePublishedDocumentViews(document);

        PublishedDocumentData documentData = publishedDocumentDataService.constructPublishedDocumentData(document);
        loadAnnotationIfPresent(document, user, documentData);

        return documentData;
    }

    public boolean existsByHash(String hash) {
        return publishedDocumentRepository.existsByHash(hash);
    }

    public boolean existsByHashAndPublishedStatus(String hash) {
        return publishedDocumentRepository.existsByHashAndStatus(hash, PublishedDocumentStatus.PUBLISHED);
    }

    public List<PublishedDocument> loadByUserAndStatusOrderByCreatedAtDesc(Long id, PublishedDocumentStatus status) {
        List<PublishedDocument> documents = publishedDocumentRepository.findByUserIdAndStatusOrderByCreatedAtDesc(id, status);
        return documents;
    }

    public PublishedDocument loadByHash(String hash) {
        PublishedDocument document = publishedDocumentRepository.findByHash(hash);
        return document;
    }

    public int countUsersPublishedDocuments(Long userId) {
        return publishedDocumentRepository.countByUserIdAndStatus(userId, PublishedDocumentStatus.PUBLISHED);
    }

    public void modifyPublishedDocumentUsers(Long userId, User anonymousUser) {
        List<PublishedDocument> documents = loadByUserAndStatusOrderByCreatedAtDesc(userId, PublishedDocumentStatus.PUBLISHED);
        for (PublishedDocument document : documents) {
            document.setUser(anonymousUser);
        }
        saveAll(documents);
    }

    public List<String> collectUserPublishedDocumentUrls(User user) {
        List<PublishedDocument> documents = loadByUser(user);
        List<String> urls = documents.stream().map(d -> d.getPdfUrl()).collect(Collectors.toList());
        return urls;
    }

    public List<UserPublishedDocumentAnnotationData> collectUserPublishedDocumentAnnotations(User user) {
        List<UserPublishedDocumentAnnotationData> userDocumentAnnotations = new ArrayList<>();

        List<PublishedDocumentAnnotation> annotations = publishedDocumentAnnotationService.loadAnnotationsByUser(user.getId());
        for (PublishedDocumentAnnotation annotation : annotations) {
            PublishedDocument publishedDocument = annotation.getPublishedDocument();
            String documentUrl = publishedDocumentDataService.constructPublishedDocumentUrl(publishedDocument.getHash());

            UserPublishedDocumentAnnotationData userDocumentAnnotation = new UserPublishedDocumentAnnotationData.UserPublishedDocumentAnnotationDataBuilder()
                    .setPublishedDocumentUrl(documentUrl).setAnnotation(annotation.getAnnotation()).build();
            userDocumentAnnotations.add(userDocumentAnnotation);
        }

        return userDocumentAnnotations;
    }

    public void saveAnnotation(String hash, User user, String annotationText) {
        PublishedDocument document = loadByHash(hash);
        publishedDocumentAnnotationService.save(document, user, annotationText);
    }

    public boolean isDocumentPublished(String hash) {
        PublishedDocument document = loadByHash(hash);
        PublishedDocumentStatus status = document.getStatus();

        boolean published = PublishedDocumentStatus.PUBLISHED == status;
        return published;
    }

    public PublishedDocument loadById(Long publishedDocumentId) {
        Optional<PublishedDocument> optionalDocument = publishedDocumentRepository.findById(publishedDocumentId);
        PublishedDocument document = optionalDocument.isPresent() ? optionalDocument.get() : null;
        return document;
    }

    public PublishedDocument constructWithoutPublishedDataAndSave(Document document, User user) {
        PublishedDocument publishedDocument = publishedDocumentDataService.constructPublishedDocumentWithoutPublishDataAndOcr(document, user);
        PublishedDocument savedPublishedDocument = save(publishedDocument);
        Long publishedDocumentId = savedPublishedDocument.getId();

        document.setPublishedDocument(savedPublishedDocument);
        documentService.save(document);

        String publishedDocumentLog = MessageFormat.format("Created empty Published Document. Document ID: {0}. Published Document ID: {1}.", document.getId(),
                publishedDocumentId);
        logger.info(publishedDocumentLog);

        prometheusReporterService.updateCreateInProgressPublishedDocumentCounter(publishedDocumentId);

        return savedPublishedDocument;
    }

    public boolean userOcrRateLimitReached(Document document, User user) {
        int userOcrRateLimit = ocrService.loadUserOcrRateLimit(user);

        int imagesWithoutOcr = countImagesWithoutOcr(document);
        int updatedUserOcrLimit = userOcrRateLimit + imagesWithoutOcr;

        boolean userOcrRateLimitReached = updatedUserOcrLimit > ocrLimit;
        return userOcrRateLimitReached;
    }

    public void constructPublishedDocumentOcrsAndSave(Long publishedDocumentId, Long documentId) {
        PublishedDocument publishedDocument = loadById(publishedDocumentId);
        Document document = documentService.loadById(documentId);

        publishedDocumentDataService.constructPublishedDocumentOcrs(document, publishedDocument);
        save(publishedDocument);
    }

    private String uploadPdf(ByteArrayOutputStream os, Document document, String documentHash, Long userId, Errors errors) throws MediaUploadException {
        String pdfUrl = null;

        try (InputStream is = new ByteArrayInputStream(os.toByteArray())) {
            String key = MessageFormat.format(PUBLISHED_DOCUMENT_S3_KEY, documentHash);
            pdfUrl = mediaService.uploadInputStream(is, key, MediaType.APPLICATION_PDF_VALUE, userId, errors);
        } catch (IOException e) {
            String logMessage = MessageFormat.format(
                    "action=uploadPdf, status=warning, error=Could not close InputStream resource after uploading to S3 bucket., documentId={0}, url={1}", document.getId(),
                    pdfUrl);
            logger.warn(logMessage, e);
        }

        String logMessage = MessageFormat.format("action=uploadPdf, status=success, documentId={0}, url={1}", document.getId(), pdfUrl);
        logger.info(logMessage);

        return pdfUrl;
    }

    private String uploadPdfThumbnail(Document document, String documentHash, Errors errors) throws MediaCopyException {
        DocumentImage image = document.getImages().get(0);
        String imageUrl = StringUtils.hasText(image.getThumbnailUrl()) ? image.getThumbnailUrl() : image.getUrl();
        String pdfThumbnailName = MessageFormat.format(PUBLISHED_DOCUMENT_THUMBNAIL_S3_KEY, documentHash);
        String url = mediaService.uploadPdfThumbnail(imageUrl, pdfThumbnailName, errors);
        return url;
    }

    private PublishedDocument save(PublishedDocument publishedDocument) {
        return publishedDocumentRepository.save(publishedDocument);
    }

    private List<PublishedDocument> loadPublishedDocuments(PublicArchivePageFilteringRequest publicArchivePageFilteringRequest, String searchTerm) {
        List<PublishedDocument> publishedDocuments = new ArrayList<>();

        PeriodFilterData periodFilter = publicArchivePageFilteringRequest.getPeriodFilter();
        PublishedDocumentStatus status = PublishedDocumentStatus.PUBLISHED;

        if (StringUtils.hasText(searchTerm)) {
            publishedDocuments = loadPublishedDocumentsByPeriodAndSearchTermAndStatus(periodFilter, searchTerm, status);
        } else {
            publishedDocuments = loadPublishedDocumentsByPeriodAndStatus(periodFilter, status);
        }

        return publishedDocuments;
    }

    private List<PublishedDocument> loadPublishedDocumentsByPeriodAndSearchTermAndStatus(PeriodFilterData periodFilter, String searchTerm, PublishedDocumentStatus status) {
        List<PublishedDocument> publishedDocuments = new ArrayList<>();

        List<Long> searchResultDocumentIds = publishedDocumentMetaDataIndexService.searchPublishedDocument(searchTerm);
        if (!searchResultDocumentIds.isEmpty()) {
            publishedDocuments = loadPublishedDocumentsByPeriodAndIdAndStatus(periodFilter, searchResultDocumentIds, status);
        }

        return publishedDocuments;
    }

    private List<PublishedDocument> loadPublishedDocumentsByPeriodAndIdAndStatus(PeriodFilterData periodFilter, List<Long> searchResultDocumentIds,
            PublishedDocumentStatus status) {
        Integer periodFrom = periodFilter.getFrom();
        Integer periodTo = periodFilter.getTo();

        boolean isPeriodFromSet = null != periodFrom;
        boolean isPeriodToSet = null != periodTo;

        List<PublishedDocument> publishedDocuments = new ArrayList<>();
        if (isPeriodFromSet && isPeriodToSet) {
            publishedDocuments = publishedDocumentRepository
                    .findByPublishedDocumentMetaDataPeriodCoveredFromGreaterThanEqualAndPublishedDocumentMetaDataPeriodCoveredToLessThanEqualAndIdInAndStatusOrderByCreatedAtDesc(
                            periodFrom, periodTo, searchResultDocumentIds, status);
        } else if (isPeriodFromSet) {
            publishedDocuments = publishedDocumentRepository
                    .findByPublishedDocumentMetaDataPeriodCoveredFromGreaterThanEqualAndIdInAndStatusOrderByCreatedAtDesc(
                            periodFrom, searchResultDocumentIds, status);
        } else if (isPeriodToSet) {
            publishedDocuments = publishedDocumentRepository
                    .findByPublishedDocumentMetaDataPeriodCoveredToLessThanEqualAndIdInAndStatusOrderByCreatedAtDesc(
                            periodTo, searchResultDocumentIds, status);
        } else {
            publishedDocuments = publishedDocumentRepository.findByIdInAndStatusOrderByCreatedAtDesc(searchResultDocumentIds, status);
        }

        return publishedDocuments;
    }

    private List<PublishedDocument> loadPublishedDocumentsByPeriodAndStatus(PeriodFilterData periodFilter, PublishedDocumentStatus status) {
        List<PublishedDocument> publishedDocuments = new ArrayList<>();
        if (useFastPublicArchivesQuery) {
            publishedDocuments = loadPublishedDocumentsByPeriodAndStatusWithFastQuery(periodFilter, status);
        } else {
            publishedDocuments = loadPublishedDocumentsByPeriodAndStatusWithNormalQuery(periodFilter, status);
        }

        return publishedDocuments;
    }

    private List<PublishedDocument> loadPublishedDocumentsByPeriodAndStatusWithFastQuery(PeriodFilterData periodFilter, PublishedDocumentStatus status) {
        Integer periodFrom = periodFilter.getFrom();
        Integer periodTo = periodFilter.getTo();

        boolean isPeriodFromSet = null != periodFrom;
        boolean isPeriodToSet = null != periodTo;

        List<PublishedDocument> publishedDocuments = new ArrayList<>();
        if (isPeriodFromSet && isPeriodToSet) {
            publishedDocuments = publishedDocumentRepository
                    .findFirst20ByPublishedDocumentMetaDataPeriodCoveredFromGreaterThanEqualAndPublishedDocumentMetaDataPeriodCoveredToLessThanEqualAndStatusOrderByCreatedAtDesc(
                            periodFrom, periodTo, status);
        } else if (isPeriodFromSet) {
            publishedDocuments = publishedDocumentRepository.findFirst20ByPublishedDocumentMetaDataPeriodCoveredFromGreaterThanEqualAndStatusOrderByCreatedAtDesc(periodFrom,
                    status);
        } else if (isPeriodToSet) {
            publishedDocuments = publishedDocumentRepository
                    .findFirst20ByPublishedDocumentMetaDataPeriodCoveredToLessThanEqualAndStatusOrderByCreatedAtDesc(
                            periodTo, status);
        } else {
            publishedDocuments = publishedDocumentRepository.findFirst20ByStatusOrderByCreatedAtDesc(status);
        }
        
        return publishedDocuments;
    }

    private List<PublishedDocument> loadPublishedDocumentsByPeriodAndStatusWithNormalQuery(PeriodFilterData periodFilter, PublishedDocumentStatus status) {
        Integer periodFrom = periodFilter.getFrom();
        Integer periodTo = periodFilter.getTo();

        boolean isPeriodFromSet = null != periodFrom;
        boolean isPeriodToSet = null != periodTo;

        List<PublishedDocument> publishedDocuments = new ArrayList<>();
        if (isPeriodFromSet && isPeriodToSet) {
            publishedDocuments = publishedDocumentRepository
                    .findByPublishedDocumentMetaDataPeriodCoveredFromGreaterThanEqualAndPublishedDocumentMetaDataPeriodCoveredToLessThanEqualAndStatusOrderByCreatedAtDesc(
                            periodFrom, periodTo, status);
        } else if (isPeriodFromSet) {
            publishedDocuments = publishedDocumentRepository.findByPublishedDocumentMetaDataPeriodCoveredFromGreaterThanEqualAndStatusOrderByCreatedAtDesc(periodFrom, status);
        } else if (isPeriodToSet) {
            publishedDocuments = publishedDocumentRepository.findByPublishedDocumentMetaDataPeriodCoveredToLessThanEqualAndStatusOrderByCreatedAtDesc(periodTo, status);
        } else {
            publishedDocuments = publishedDocumentRepository.findByStatusOrderByCreatedAtDesc(status);
        }

        return publishedDocuments;
    }

    private List<PublishedDocument> filterPublishedDocuments(List<PublishedDocument> publishedDocuments, PublicArchivePageFilteringRequest publicArchivePageFilteringRequest) {
        List<DocumentTag> activeTags = publicArchivePageFilteringRequest.getActiveTags();
        if (!activeTags.isEmpty()) {
            publishedDocuments = publishedDocuments.stream().filter(d -> d.getPublishedDocumentMetaData().getTags().containsAll(activeTags)).collect(Collectors.toList());
        }

        List<PublishedDocument> filteredPublishedDocuments = publishedDocuments.stream()
                .filter(d -> isDocumentFilterResult(d.getPublishedDocumentMetaData().getDocumentFilters(), publicArchivePageFilteringRequest))
                .collect(Collectors.toList());
        return filteredPublishedDocuments;
    }

    private boolean isDocumentFilterResult(List<DocumentFilter> documentFilters, PublicArchivePageFilteringRequest publicArchivePageFilteringRequest) {
        Map<DocumentFilterTypeName, List<DocumentFilter>> activeFilters = publicArchivePageFilteringRequest.getActiveFilters();

        boolean isDocumentFilterResult = true;

        for (DocumentFilterTypeName filterTypeName : DocumentFilterTypeName.values()) {
            isDocumentFilterResult = documentContainsAnyFilter(documentFilters, activeFilters, filterTypeName);
            if (!isDocumentFilterResult) {
                break;
            }
        }

        return isDocumentFilterResult;
    }

    private boolean documentContainsAnyFilter(List<DocumentFilter> documentFilters, Map<DocumentFilterTypeName, List<DocumentFilter>> filters,
            DocumentFilterTypeName type) {
        boolean isDocumentFilterResult = true;
        if (filters.containsKey(type) && !filters.get(type).isEmpty()) {
            List<DocumentFilter> filtersByType = filters.get(type);
            isDocumentFilterResult = CollectionUtils.containsAny(documentFilters, filtersByType);
        }
        return isDocumentFilterResult;
    }

    private void updatePublishedDocumentViews(PublishedDocument document) {
        int views = document.getViews();
        views++;
        document.setViews(views);
        save(document);
    }

    private void loadAnnotationIfPresent(PublishedDocument document, User user, PublishedDocumentData documentData) {
        if (null != user) {
            PublishedDocumentAnnotation annotation = publishedDocumentAnnotationService.loadByPublishedDocumentAndUser(document.getId(), user.getId());
            if (null != annotation) {
                String annotationText = annotation.getAnnotation();
                documentData.setAnnotation(annotationText);
            }
        }
    }

    private void saveAll(List<PublishedDocument> documents) {
        publishedDocumentRepository.saveAll(documents);
    }

    private List<PublishedDocument> loadByUser(User user) {
        List<PublishedDocument> documents = publishedDocumentRepository.findByUser(user);
        return documents;
    }

}
