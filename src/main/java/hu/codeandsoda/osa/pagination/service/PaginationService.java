package hu.codeandsoda.osa.pagination.service;

import java.text.MessageFormat;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import hu.codeandsoda.osa.document.data.DocumentData;
import hu.codeandsoda.osa.documentpublish.data.PublishedDocumentData;
import hu.codeandsoda.osa.general.data.ResponseId;
import hu.codeandsoda.osa.general.data.ResponseMessage;
import hu.codeandsoda.osa.general.data.ResponseMessageScope;
import hu.codeandsoda.osa.myshoebox.data.ImageData;
import hu.codeandsoda.osa.pagination.data.PaginationRequest;
import hu.codeandsoda.osa.util.ErrorCode;

@Service
public class PaginationService {

    private static Logger logger = LoggerFactory.getLogger(PaginationService.class);

    @Value("${osa.published-documents.display.size.max}")
    private int publishedDocumentsDisplaySizeMax;

    @Value("${osa.myshoebox.display.size.max}")
    private int myShoeBoxDisplaySizeMax;

    @Value("${osa.documents.display.size.max}")
    private int documentsDisplaySizeMax;

    public Page<PublishedDocumentData> createPublishedDocumentDataPage(List<PublishedDocumentData> publishedDocumentDatas, PaginationRequest paginationRequest,
            List<ResponseMessage> messages) {
        int dataSize = publishedDocumentDatas.size();
        String errorData = "action=loadPublishedDocuments, ";

        int size = calculateValidPageSize(paginationRequest.getSize(), publishedDocumentsDisplaySizeMax, messages, errorData);
        int page = calculateValidPage(paginationRequest.getPage(), messages, errorData);

        Pageable pageable = createPageable(page, size, dataSize, messages, errorData);

        int start = (int) pageable.getOffset();
        int end = calculatePageEnd(pageable, start, dataSize);

        Page<PublishedDocumentData> publishedDocumentDataPage = new PageImpl<PublishedDocumentData>(publishedDocumentDatas.subList(start, end), pageable,
                publishedDocumentDatas.size());
        return publishedDocumentDataPage;
    }

    public Page<ImageData> createImageDataPage(List<ImageData> imageDatas, int size, int page, List<ResponseMessage> messages, Long myShoeBoxId) {
        int dataSize = imageDatas.size();
        String errorData = MessageFormat.format("action=loadMyShoeBox, myshoeboxId={0}, ", myShoeBoxId);
        
        size = calculateValidPageSize(size, myShoeBoxDisplaySizeMax, messages, errorData);
        page = calculateValidPage(page, messages, errorData);

        Pageable pageable = createPageable(page, size, dataSize, messages, errorData);

        int start = (int) pageable.getOffset();
        int end = calculatePageEnd(pageable, start, imageDatas.size());

        Page<ImageData> imageDataPage = new PageImpl<ImageData>(imageDatas.subList(start, end), pageable, imageDatas.size());
        return imageDataPage;
    }

    public Page<DocumentData> createDocumentDataPage(List<DocumentData> documentDatas, List<ResponseMessage> messages, int size, int page, Long userId) {
        int dataSize = documentDatas.size();
        String errorData = MessageFormat.format("action=loadDocuments, userId={0}, ", userId);

        size = calculateValidPageSize(size, documentsDisplaySizeMax, messages, errorData);
        page = calculateValidPage(page, messages, errorData);

        Pageable pageable = createPageable(page, size, dataSize, messages, errorData);

        int start = (int) pageable.getOffset();
        int end = calculatePageEnd(pageable, start, documentDatas.size());

        Page<DocumentData> documentDataPage = new PageImpl<DocumentData>(documentDatas.subList(start, end), pageable, documentDatas.size());
        return documentDataPage;
    }

    private int calculateValidPageSize(int size, int displaySizeMax, List<ResponseMessage> messages, String errorData) {
        if (size < 1 || size > displaySizeMax) {
            String errorCode = ErrorCode.INVALID_PAGE_SIZE.toString();
            String logMessage = new StringBuilder().append(errorData).append("status=warning, error=").append(errorCode).append(", sizeInput=")
                    .append(size)
                    .toString();
            logger.warn(logMessage);

            ResponseMessage message = new ResponseMessage.ResponseMessageBuilder().setId(ResponseId.PAGE_SIZE).setResponseMessageScope(ResponseMessageScope.WARNING)
                    .setMessage("Invalid page size: " + size + ". Defaults to max value: " + displaySizeMax).build();
            messages.add(message);

            size = displaySizeMax;
        }
        return size;
    }

    private int calculateValidPage(int page, List<ResponseMessage> messages, String errorData) {
        if (page < 0) {
            page = 0;
            handleInvalidPageWarning(messages, page, errorData);
        }
        return page;
    }

    private Pageable createPageable(int page, int size, int dataSize, List<ResponseMessage> messages, String errorData) {
        Pageable pageable = PageRequest.of(page, size);
        int start = (int) pageable.getOffset();
        if (start > dataSize) {
            pageable = PageRequest.of(0, size);
            handleInvalidPageWarning(messages, page, errorData);
        }
        return pageable;
    }

    private int calculatePageEnd(Pageable pageable, int start, int dataSize) {
        int end = (start + pageable.getPageSize()) > dataSize ? dataSize : (start + pageable.getPageSize());
        return end;
    }

    private void handleInvalidPageWarning(List<ResponseMessage> messages, int page, String errorData) {
        String errorCode = ErrorCode.INVALID_PAGE.toString();
        String logMessage = new StringBuilder().append(errorData).append("status=warning, error=").append(errorCode).append(", pageInput=")
                .append(page).toString();
        logger.warn(logMessage);

        ResponseMessage message = new ResponseMessage.ResponseMessageBuilder().setId(ResponseId.PAGE).setResponseMessageScope(ResponseMessageScope.WARNING)
                .setMessage("Invalid page: " + page).build();
        messages.add(message);
    }

}
