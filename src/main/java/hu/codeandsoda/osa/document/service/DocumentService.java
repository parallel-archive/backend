package hu.codeandsoda.osa.document.service;

import java.text.MessageFormat;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;

import hu.codeandsoda.osa.account.user.data.UserDocumentData;
import hu.codeandsoda.osa.account.user.domain.User;
import hu.codeandsoda.osa.document.data.DocumentData;
import hu.codeandsoda.osa.document.data.DocumentImageRequestData;
import hu.codeandsoda.osa.document.data.DocumentImagesRequestData;
import hu.codeandsoda.osa.document.data.DocumentMetaDataRequestData;
import hu.codeandsoda.osa.document.data.DocumentRequestData;
import hu.codeandsoda.osa.document.data.DocumentsData;
import hu.codeandsoda.osa.document.domain.Document;
import hu.codeandsoda.osa.document.domain.DocumentImage;
import hu.codeandsoda.osa.document.domain.DocumentMetaData;
import hu.codeandsoda.osa.document.domain.DocumentTag;
import hu.codeandsoda.osa.document.repository.DocumentRepository;
import hu.codeandsoda.osa.documentfilter.domain.DocumentFilter;
import hu.codeandsoda.osa.documentfilter.service.DocumentFilterService;
import hu.codeandsoda.osa.documentpublish.domain.PublishedDocument;
import hu.codeandsoda.osa.exception.ValidationException;
import hu.codeandsoda.osa.general.data.ResponseMessage;
import hu.codeandsoda.osa.jms.service.image.DeleteImageMessageService;
import hu.codeandsoda.osa.media.exception.MediaCopyException;
import hu.codeandsoda.osa.media.service.MediaService;
import hu.codeandsoda.osa.myshoebox.data.DeleteImagesData;
import hu.codeandsoda.osa.myshoebox.domain.Image;
import hu.codeandsoda.osa.myshoebox.service.ImageService;
import hu.codeandsoda.osa.pagination.service.PaginationService;
import hu.codeandsoda.osa.sort.service.SortDocumentsService;
import hu.codeandsoda.osa.util.ErrorCode;

@Service
public class DocumentService {

    private static final String LOAD_DOCUMENT_BY_ID_ACTION = "loadDocumentById";
    private static final String DOCUMENT_LOAD_ERROR_MESSAGE = "Could not load document";
    private static final String DELETE_DOCUMENT_BY_ID_ACTION = "deleteDocumentById";
    private static final String DELETE_DOCUMENT_ERROR_MESSAGE = "Could not delete document";

    private static Logger logger = LoggerFactory.getLogger(DocumentService.class);

    @Autowired
    private ImageService imageService;

    @Autowired
    private MediaService mediaService;

    @Autowired
    private DocumentDataService documentDataService;

    @Autowired
    private DocumentUtil documentUtil;

    @Autowired
    private PaginationService paginationService;

    @Autowired
    private DeleteImageMessageService deleteImageMessageService;

    @Autowired
    private DocumentTagService documentTagService;

    @Autowired
    private DocumentFilterService documentFilterService;

    @Autowired
    private SortDocumentsService sortDocumentsService;

    @Autowired
    private DocumentRepository documentRepository;

    @Transactional
    public DocumentData saveDocumentImages(User user, DocumentImagesRequestData documentImagesRequestData, Errors errors) throws MediaCopyException {
        Long documentId = documentImagesRequestData.getId();
        Document document = null;
        int index = 0;
        if (null != documentId) {
            document = loadById(documentId);
            document.setModifiedAt(ZonedDateTime.now());
            index = document.getImages().size();
        } else {
            document = saveEmptyDocument(user);
            documentId = document.getId();
        }

        List<DocumentImage> documentImages = document.getImages();
        for (Long imageId : documentImagesRequestData.getImageIds()) {
            Image originalImage = imageService.loadById(imageId);
            String originalImageName = originalImage.getName();

            DocumentImage documentImage = new DocumentImage();
            documentImage.setName(originalImage.getName());
            documentImage.setIndex(index);
            
            String originalImageUrl = StringUtils.hasText(originalImage.getActiveUrl()) ? originalImage.getActiveUrl() : originalImage.getUrl();
            String url = mediaService.uploadDocumentImage(originalImageUrl, originalImageName, documentId, errors);
            documentImage.setUrl(url);
            
            String originalThumbnailUrl = StringUtils.hasText(originalImage.getActiveThumbnailUrl()) ? originalImage.getActiveThumbnailUrl() : originalImage.getThumbnailUrl();
            if (StringUtils.hasText(originalThumbnailUrl)) {
                String thumbnailUrl = mediaService.uploadDocumentImage(originalThumbnailUrl, originalImageName, documentId, errors);
                documentImage.setThumbnailUrl(thumbnailUrl);
            }

            documentImage.setUploadedAt(document.getUploadedAt());
            documentImage.setDocument(document);

            documentImages.add(documentImage);
            index++;
        }
        document.setImages(documentImages);

        Document savedDocument = save(document);
        DocumentData savedDocumentData = documentDataService.constructDocumentData(savedDocument);
        return savedDocumentData;
    }

    public DocumentData loadEditableDocumentByIdAndUser(Long id, User user, Errors errors) throws ValidationException {
        Long userId = user.getId();
        if (!userDocumentExist(id, userId)) {
            handleLoadEditableDocumentError(id, userId, LOAD_DOCUMENT_BY_ID_ACTION, DOCUMENT_LOAD_ERROR_MESSAGE, ErrorCode.DOCUMENT_NOT_FOUND, errors);
        }

        Document document = loadById(id);
        if (null != document.getPublishedDocument()) {
            handleLoadEditableDocumentError(id, userId, LOAD_DOCUMENT_BY_ID_ACTION, DOCUMENT_LOAD_ERROR_MESSAGE, ErrorCode.DOCUMENT_ALREADY_PUBLISHED, errors);
        }

        DocumentData documentData = documentDataService.constructDocumentData(document);
        return documentData;
    }

    public DocumentData loadDocumentDataById(Long documentId) {
        Document document = loadById(documentId);
        DocumentData documentData = documentDataService.constructDocumentData(document);
        return documentData;
    }

    public Document loadById(Long id) {
        Optional<Document> optionalDocument = documentRepository.findById(id);
        Document document = optionalDocument.isPresent() ? optionalDocument.get() : null;
        return document;
    }

    public List<Document> loadAllByUser(User user) {
        List<Document> documents = documentRepository.findAllByUser(user);
        return documents;
    }

    public void deleteUserDocuments(User user) {
        List<Document> documents = loadAllByUser(user);
        List<String> urls = documentUtil.collectAllDocumentImageUrls(documents);

        documentRepository.deleteAll(documents);
        addUrlsToDeleteImageQueue(urls);
    }

    public void deleteByIdAndUser(Long id, Long userId, Errors errors) throws ValidationException {
        if (!userDocumentExist(id, userId)) {
            handleLoadEditableDocumentError(id, userId, DELETE_DOCUMENT_BY_ID_ACTION, DELETE_DOCUMENT_ERROR_MESSAGE, ErrorCode.DOCUMENT_NOT_FOUND, errors);
        }

        Document document = loadById(id);
        if (null != document.getPublishedDocument()) {
            handleLoadEditableDocumentError(id, userId, DELETE_DOCUMENT_BY_ID_ACTION, DELETE_DOCUMENT_ERROR_MESSAGE, ErrorCode.DOCUMENT_ALREADY_PUBLISHED, errors);
        }

        List<String> urls = documentUtil.collectDocumentImageUrls(document);

        documentRepository.delete(document);
        addUrlsToDeleteImageQueue(urls);
        String logMessage = MessageFormat.format("action=deleteDocumentById, status=success, userId={0}, documentId={1}", userId, id);
        logger.info(logMessage);
    }

    public void deleteOriginalOfPublishedDocumentByIdAndUser(Long id, Long userId, Errors errors) throws ValidationException {
        if (!userDocumentExist(id, userId)) {
            handleLoadEditableDocumentError(id, userId, DELETE_DOCUMENT_BY_ID_ACTION, DELETE_DOCUMENT_ERROR_MESSAGE, ErrorCode.DOCUMENT_NOT_FOUND, errors);
        }

        Document document = loadById(id);
        List<String> urls = documentUtil.collectDocumentImageUrls(document);

        documentRepository.delete(document);
        addUrlsToDeleteImageQueue(urls);
        String logMessage = MessageFormat.format("action=deleteDocumentById, status=success, userId={0}, documentId={1}", userId, id);
        logger.info(logMessage);
    }

    public boolean userDocumentExist(Long documentId, Long userId) {
        return documentRepository.existsByIdAndUserId(documentId, userId);
    }

    public DocumentsData loadEditableDocuments(User user, String sort, String sortBy, int size, int page) {
        Long userId = user.getId();

        List<Document> documents = documentRepository.findAllByUserAndPublishedDocumentIsNullOrderByModifiedAtDesc(user);
        List<DocumentData> documentDatas = documentDataService.constructDocumentDatas(documents);
        List<ResponseMessage> messages = new ArrayList<>();

        sortDocumentsService.sortDocuments(documentDatas, sort, sortBy, userId, messages);
        Page<DocumentData> documentDataPage = paginationService.createDocumentDataPage(documentDatas, messages, size, page, userId);

        DocumentsData documentsData = new DocumentsData.DocumentsDataBuilder().setDocumentDataPage(documentDataPage).setMessages(messages).build();
        return documentsData;
    }

    @Transactional
    public DocumentData editDocument(DocumentRequestData documentRequestData, Errors errors) {
        Long documentId = documentRequestData.getId();
        Document document = loadById(documentId);
        document.setModifiedAt(ZonedDateTime.now());

        updateDocumentMetaData(document, documentRequestData.getMetaDataRequest());
        updateDocumentImages(document, documentRequestData.getImages());

        Document updatedDocument = save(document);
        DocumentData documentData = documentDataService.constructDocumentData(updatedDocument);
        return documentData;
    }

    public List<UserDocumentData> collectUserDocuments(User user) {
        List<Document> documents = loadAllByUser(user);
        List<UserDocumentData> documentDatas = documentDataService.constructUserDocumentDatas(documents);
        return documentDatas;
    }

    public Document save(Document document) {
        return documentRepository.save(document);
    }

    public Document loadByPublishedDocument(PublishedDocument publishedDocument) {
        Document document = documentRepository.findByPublishedDocument(publishedDocument);
        return document;
    }

    public boolean isPublishInProgress(Document document) {
        boolean publishInProgress = null != document.getPublishedDocument();
        return publishInProgress;
    }

    public int loadUsersDocumentsSize(Long userId) {
        return documentRepository.countByUserIdAndPublishedDocumentIsNull(userId);
    }

    private Document saveEmptyDocument(User user) {
        Document document = new Document();
        document.setImages(new ArrayList<>());
        DocumentMetaData documentMetaData = new DocumentMetaData();
        documentMetaData.setDocumentFilters(new ArrayList<>());
        document.setMetaData(documentMetaData);
        ZonedDateTime now = ZonedDateTime.now();
        document.setUploadedAt(now);
        document.setModifiedAt(now);
        document.setUser(user);

        Document savedDocument = save(document);
        return savedDocument;
    }

    private void handleLoadEditableDocumentError(Long documentId, Long userId, String action, String errorMessage, ErrorCode errorCode, Errors errors) throws ValidationException {
        String error = errorCode.toString();
        String logMessage = MessageFormat.format("action={0}, status=error, userId={1}, documentId={2}, error={3}", action, userId, documentId, error);
        logger.error(logMessage);

        errors.reject(error, errorMessage);
        throw new ValidationException(errorMessage, errors.getAllErrors());
    }

    private void addUrlsToDeleteImageQueue(List<String> urls) {
        deleteImageMessageService.addToDeleteImageQueue(new DeleteImagesData.DeleteImagesDataBuilder().setUrls(urls).build());
        String logMessage = MessageFormat.format("action=addDocumentImageUrlsToDeleteImageQueue, status=success, size={0}", urls.size());
        logger.info(logMessage);
    }

    private void updateDocumentMetaData(Document document, DocumentMetaDataRequestData documentMetaDataRequestData) {
        Long metaDataId = document.getMetaData().getId();

        DocumentMetaData documentMetaData = new DocumentMetaData();
        documentMetaData.setId(metaDataId);
        if (null != documentMetaDataRequestData) {
            documentMetaData.setOriginalTitle(documentMetaDataRequestData.getOriginalTitle());
            documentMetaData.setOriginalAuthor(documentMetaDataRequestData.getOriginalAuthor());
            documentMetaData.setCreatedAtYear(documentMetaDataRequestData.getCreatedAtYear());
            documentMetaData.setPeriodCoveredFrom(documentMetaDataRequestData.getPeriodCoveredFrom());
            documentMetaData.setPeriodCoveredTo(documentMetaDataRequestData.getPeriodCoveredTo());
            documentMetaData.setArchiveName(documentMetaDataRequestData.getArchiveName());
            documentMetaData.setArchiveCategory(documentMetaDataRequestData.getArchiveCategory());
            documentMetaData.setPublication(documentMetaDataRequestData.getPublication());
            documentMetaData.setCatalogUrl(documentMetaDataRequestData.getCatalogUrl());
            documentMetaData.setSourceUrl(documentMetaDataRequestData.getSourceUrl());
            documentMetaData.setReferenceCode(documentMetaDataRequestData.getReferenceCode());

            List<DocumentTag> tags = documentTagService.createAndLoadTags(documentMetaDataRequestData.getTags());
            documentMetaData.setTags(tags);

            List<DocumentFilter> activeDocumentFilters = documentFilterService.collectActiveDocumentFilters(documentMetaDataRequestData);
            documentMetaData.setDocumentFilters(activeDocumentFilters);
        }
        
        document.setMetaData(documentMetaData);
    }

    private void updateDocumentImages(Document document, List<DocumentImageRequestData> imageRequests) {
        List<DocumentImage> originalImages = document.getImages();
        Map<Long, DocumentImageRequestData> imageRequestsById = documentUtil.collectImageRequestsById(imageRequests);

        List<String> deleteUrls = new ArrayList<>();
        List<DocumentImage> deleteImages = new ArrayList<>();
        for (DocumentImage originalImage : originalImages) {
            Long id = originalImage.getId();
            if (imageRequestsById.containsKey(id)) {
                DocumentImageRequestData imageRequest = imageRequestsById.get(id);
                originalImage.setIndex(imageRequest.getIndex());
                originalImage.setEditedOcr(imageRequest.getOcr());
            } else {
                deleteUrls.addAll(documentUtil.collectImageUrls(originalImage));
                deleteImages.add(originalImage);
            }
        }

        addUrlsToDeleteImageQueue(deleteUrls);
        originalImages.removeAll(deleteImages);
    }

}
