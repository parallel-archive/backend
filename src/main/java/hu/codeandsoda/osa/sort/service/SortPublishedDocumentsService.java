package hu.codeandsoda.osa.sort.service;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import hu.codeandsoda.osa.documentpublish.domain.PublishedDocument;
import hu.codeandsoda.osa.general.data.ResponseId;
import hu.codeandsoda.osa.general.data.ResponseMessage;
import hu.codeandsoda.osa.general.data.ResponseMessageScope;
import hu.codeandsoda.osa.sort.data.PublishedDocumentSortByField;
import hu.codeandsoda.osa.sort.data.SortingRequest;
import hu.codeandsoda.osa.util.ErrorCode;

@Service
public class SortPublishedDocumentsService {

    private static final Direction DEFAULT_SORT_DIRECTION = Direction.DESC;
    private static final PublishedDocumentSortByField DEFAULT_SORT_BY_FIELD = PublishedDocumentSortByField.DATE;

    private static Logger logger = LoggerFactory.getLogger(SortPublishedDocumentsService.class);

    public void sortPublishedDocuments(List<PublishedDocument> publishedDocuments, SortingRequest sortingRequest, List<ResponseMessage> messages) {
        // By default Published Documents are loaded in descending order by Created at date
        Direction sortDirection = loadSortDirection(sortingRequest.getSort(), messages);
        PublishedDocumentSortByField sortByField = loadSortByField(sortingRequest.getSortBy(), messages);

        if (DEFAULT_SORT_BY_FIELD == sortByField && DEFAULT_SORT_DIRECTION != sortDirection) {
            Collections.reverse(publishedDocuments);
        } else if (DEFAULT_SORT_BY_FIELD != sortByField) {
            Comparator<PublishedDocument> comparator = sortByField.getComparator();
            if (DEFAULT_SORT_DIRECTION != sortDirection) {
                comparator = comparator.reversed();
            }
            Collections.sort(publishedDocuments, comparator);
        }
    }

    private PublishedDocumentSortByField loadSortByField(String sortBy, List<ResponseMessage> messages) {
        PublishedDocumentSortByField sortByField = DEFAULT_SORT_BY_FIELD;

        try {
            sortByField = PublishedDocumentSortByField.valueOf(sortBy);
        } catch (Exception e) {
            String errorCode = ErrorCode.INVALID_SORT_BY_FIELD.toString();
            String logMessage = new StringBuilder().append("action=loadPublishedDocuments, status=warning, error=").append(errorCode).append(", sortByInput=").append(sortBy)
                    .toString();
            logger.warn(logMessage, e);

            ResponseMessage message = new ResponseMessage.ResponseMessageBuilder().setId(ResponseId.SORT_BY_FIELD).setResponseMessageScope(ResponseMessageScope.WARNING)
                    .setMessage("Invalid sort by field: " + sortBy + ". Defaults to DATE.").build();
            messages.add(message);
        }

        return sortByField;
    }

    private Direction loadSortDirection(String sort, List<ResponseMessage> messages) {
        Direction sortDirection = DEFAULT_SORT_DIRECTION;

        try {
            sortDirection = Direction.fromString(sort);
        } catch (Exception e) {
            String errorCode = ErrorCode.INVALID_SORT_DIRECTION.toString();
            String logMessage = new StringBuilder().append("action=loadPublishedDocuments, status=warning, error=").append(errorCode).append(", sortInput=").append(sort)
                    .toString();
            logger.warn(logMessage, e);

            ResponseMessage message = new ResponseMessage.ResponseMessageBuilder().setId(ResponseId.SORT_DIRECTION).setResponseMessageScope(ResponseMessageScope.WARNING)
                    .setMessage("Invalid sort direction: " + sort + ". Valid directions: ASC, DESC. Defaults to DESC.").build();
            messages.add(message);
        }

        return sortDirection;
    }

}
