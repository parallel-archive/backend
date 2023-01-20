package hu.codeandsoda.osa.sort.service;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import hu.codeandsoda.osa.general.data.ResponseId;
import hu.codeandsoda.osa.general.data.ResponseMessage;
import hu.codeandsoda.osa.general.data.ResponseMessageScope;
import hu.codeandsoda.osa.myshoebox.data.ImageData;
import hu.codeandsoda.osa.sort.data.MyShoeBoxSortByField;
import hu.codeandsoda.osa.util.ErrorCode;

@Service
public class SortMyShoeBoxService {

    private static final Direction DEFAULT_SORT_DIRECTION = Direction.DESC;
    private static final MyShoeBoxSortByField DEFAULT_SORT_BY_FIELD = MyShoeBoxSortByField.DATE;

    private static Logger logger = LoggerFactory.getLogger(SortMyShoeBoxService.class);

    public void sortImages(List<ImageData> images, String sort, String sortBy, List<ResponseMessage> messages, Long myShoeBoxId) {
        // By default Images are loaded in descending order by Modified at date
        Direction sortDirection = loadSortDirection(sort, myShoeBoxId, messages);
        MyShoeBoxSortByField sortByField = loadSortByField(sortBy, myShoeBoxId, messages);

        if (DEFAULT_SORT_BY_FIELD == sortByField && DEFAULT_SORT_DIRECTION != sortDirection) {
            Collections.reverse(images);
        } else if (DEFAULT_SORT_BY_FIELD != sortByField) {
            Comparator<ImageData> comparator = sortByField.getComparator();
            if (DEFAULT_SORT_DIRECTION != sortDirection) {
                comparator = comparator.reversed();
            }
            Collections.sort(images, comparator);
        }
    }

    private Direction loadSortDirection(String sort, Long myShoeBoxId, List<ResponseMessage> messages) {
        Direction sortDirection = DEFAULT_SORT_DIRECTION;

        try {
            sortDirection = Direction.fromString(sort);
        } catch (Exception e) {
            String errorCode = ErrorCode.INVALID_SORT_DIRECTION.toString();
            String logMessage = MessageFormat.format("action=loadMyShoeBox, status=warning, myShoeBoxId={0}, error={1}, sortInput={2}", myShoeBoxId, errorCode, sort);
            logger.warn(logMessage);

            String responseMessage = MessageFormat.format("Invalid sort direction: {0}. Valid directions: ASC, DESC. Defaults to DESC.", sort);
            ResponseMessage message = new ResponseMessage.ResponseMessageBuilder().setId(ResponseId.SORT_DIRECTION).setResponseMessageScope(ResponseMessageScope.WARNING)
                    .setMessage(responseMessage).build();
            messages.add(message);
        }

        return sortDirection;
    }

    private MyShoeBoxSortByField loadSortByField(String sortBy, Long myShoeBoxId, List<ResponseMessage> messages) {
        MyShoeBoxSortByField sortByField = DEFAULT_SORT_BY_FIELD;

        try {
            sortByField = MyShoeBoxSortByField.valueOf(sortBy);
        } catch (Exception e) {
            String errorCode = ErrorCode.INVALID_SORT_BY_FIELD.toString();
            String logMessage = MessageFormat.format("action=loadMyShoeBox, myShoeBoxId={0}, status=warning, error={1}, sortByInput={2}", myShoeBoxId, errorCode, sortBy);
            logger.warn(logMessage, e);

            String responseMessage = MessageFormat.format("Invalid sort by field: {0}. Defaults to DATE.", sortBy);
            ResponseMessage message = new ResponseMessage.ResponseMessageBuilder().setId(ResponseId.SORT_BY_FIELD).setResponseMessageScope(ResponseMessageScope.WARNING)
                    .setMessage(responseMessage).build();
            messages.add(message);
        }

        return sortByField;
    }

}
