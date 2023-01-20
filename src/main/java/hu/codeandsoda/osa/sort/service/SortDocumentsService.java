package hu.codeandsoda.osa.sort.service;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import hu.codeandsoda.osa.document.data.DocumentData;
import hu.codeandsoda.osa.general.data.ResponseId;
import hu.codeandsoda.osa.general.data.ResponseMessage;
import hu.codeandsoda.osa.general.data.ResponseMessageScope;
import hu.codeandsoda.osa.sort.data.DocumentSortByField;
import hu.codeandsoda.osa.util.ErrorCode;

@Service
public class SortDocumentsService {

    private static final Direction DEFAULT_SORT_DIRECTION = Direction.DESC;
    private static final DocumentSortByField DEFAULT_SORT_BY_FIELD = DocumentSortByField.DATE;

    private static Logger logger = LoggerFactory.getLogger(SortDocumentsService.class);

    public void sortDocuments(List<DocumentData> documents, String sort, String sortBy, Long userId, List<ResponseMessage> messages) {
        // By default Documents are loaded in descending order by Modified at date
        Direction sortDirection = loadSortDirection(sort, userId, messages);
        DocumentSortByField sortByField = loadSortByField(sortBy, userId, messages);

        if (DEFAULT_SORT_BY_FIELD == sortByField && DEFAULT_SORT_DIRECTION != sortDirection) {
            Collections.reverse(documents);
        } else if (DEFAULT_SORT_BY_FIELD != sortByField) {
            Comparator<DocumentData> comparator = sortByField.getComparator();
            if (DEFAULT_SORT_DIRECTION != sortDirection) {
                comparator = comparator.reversed();
            }
            Collections.sort(documents, comparator);
        }
    }

    private Direction loadSortDirection(String sort, Long userId, List<ResponseMessage> messages) {
        // Documents are loaded in descending order by default
        Direction sortDirection = DEFAULT_SORT_DIRECTION;

        try {
            sortDirection = Direction.fromString(sort);
        } catch (Exception e) {
            String errorCode = ErrorCode.INVALID_SORT_DIRECTION.toString();
            String logMessage = MessageFormat.format("action=loadDocuments, status=warning, userId={0}, error={1}, sortInput={2}", userId, errorCode, sort);
            logger.warn(logMessage);

            String responseMessage = MessageFormat.format("Invalid sort direction: {0}. Valid directions: ASC, DESC. Defaults to DESC.", sort);
            ResponseMessage message = new ResponseMessage.ResponseMessageBuilder().setId(ResponseId.SORT_DIRECTION).setResponseMessageScope(ResponseMessageScope.WARNING)
                    .setMessage(responseMessage).build();
            messages.add(message);
        }

        return sortDirection;
    }

    private DocumentSortByField loadSortByField(String sortBy, Long userId, List<ResponseMessage> messages) {
        DocumentSortByField sortByField = DEFAULT_SORT_BY_FIELD;

        try {
            sortByField = DocumentSortByField.valueOf(sortBy);
        } catch (Exception e) {
            String errorCode = ErrorCode.INVALID_SORT_BY_FIELD.toString();
            String logMessage = MessageFormat.format("action=loadDocuments, status=warning, userId={0}, error={1}, sortByInput={2}", userId, errorCode, sortBy);
            logger.warn(logMessage);

            String responseMessage = MessageFormat.format("Invalid sort by field: {0}. Defaults to DATE.", sortBy);
            ResponseMessage message = new ResponseMessage.ResponseMessageBuilder().setId(ResponseId.SORT_DIRECTION).setResponseMessageScope(ResponseMessageScope.WARNING)
                    .setMessage(responseMessage).build();
            messages.add(message);
        }

        return sortByField;
    }

}
