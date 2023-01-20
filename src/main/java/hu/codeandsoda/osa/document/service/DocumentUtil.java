package hu.codeandsoda.osa.document.service;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import hu.codeandsoda.osa.document.data.DocumentImageRequestData;
import hu.codeandsoda.osa.document.domain.Document;
import hu.codeandsoda.osa.document.domain.DocumentImage;

@Service
public class DocumentUtil {

    private static Logger logger = LoggerFactory.getLogger(DocumentUtil.class);

    public Map<Long, DocumentImageRequestData> collectImageRequestsById(List<DocumentImageRequestData> imageRequests) {
        Map<Long, DocumentImageRequestData> imageRequestsById = new HashMap<>();
        for (DocumentImageRequestData imageRequest : imageRequests) {
            imageRequestsById.put(imageRequest.getImageId(), imageRequest);
        }
        return imageRequestsById;
    }

    public List<String> collectAllDocumentImageUrls(List<Document> documents) {
        List<String> urls = new ArrayList<>();
        for (Document document : documents) {
            List<String> documentImageUrls = collectDocumentImageUrls(document);
            urls.addAll(documentImageUrls);
        }
        return urls;
    }

    public List<String> collectDocumentImageUrls(Document document) {
        List<String> urls = new ArrayList<>();

        List<DocumentImage> images = document.getImages();
        for (DocumentImage image : images) {
            urls.addAll(collectImageUrls(image));
        }
        return urls;
    }

    public List<String> collectImageUrls(DocumentImage image) {
        List<String> urls = new ArrayList<>();

        String url = image.getUrl();
        urls.add(url);
        logger.info(MessageFormat.format("action=addDocumentImageUrlToDeleteImageQueue, status=success, url={0}", url));

        String thumbnailUrl = image.getThumbnailUrl();
        if (StringUtils.hasText(thumbnailUrl)) {
            urls.add(thumbnailUrl);
            logger.info(MessageFormat.format("action=addDocumentImageUrlToDeleteImageQueue, status=success, url={0}", thumbnailUrl));
        }

        return urls;
    }

}
