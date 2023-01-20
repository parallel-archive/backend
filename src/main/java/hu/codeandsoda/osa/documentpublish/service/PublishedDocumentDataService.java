package hu.codeandsoda.osa.documentpublish.service;

import java.text.MessageFormat;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import hu.codeandsoda.osa.account.user.domain.User;
import hu.codeandsoda.osa.document.domain.Document;
import hu.codeandsoda.osa.document.domain.DocumentImage;
import hu.codeandsoda.osa.document.domain.DocumentMetaData;
import hu.codeandsoda.osa.document.domain.DocumentTag;
import hu.codeandsoda.osa.document.service.DocumentTagDataService;
import hu.codeandsoda.osa.documentfilter.domain.DocumentFilter;
import hu.codeandsoda.osa.documentfilter.service.DocumentFilterTypeService;
import hu.codeandsoda.osa.documentpublish.data.PublishedDocumentData;
import hu.codeandsoda.osa.documentpublish.data.PublishedDocumentMetaDataData;
import hu.codeandsoda.osa.documentpublish.data.PublishedDocumentStatus;
import hu.codeandsoda.osa.documentpublish.domain.PublishedDocument;
import hu.codeandsoda.osa.documentpublish.domain.PublishedDocumentMetaData;
import hu.codeandsoda.osa.documentpublish.domain.PublishedDocumentOcr;

@Service
public class PublishedDocumentDataService {

    private static final String PUBLISHED_DOCUMENT_URL = "{0}/publication/{1}";
    private static final int USER_DISPLAY_NAME_LIMIT = 10;

    @Value("${osa.baseUrl}")
    private String baseUrl;

    @Autowired
    private DocumentTagDataService documentTagDataService;

    @Autowired
    private DocumentFilterTypeService documentFilterTypeService;

    public PublishedDocument constructPublishedDocumentWithoutPublishDataAndOcr(Document document, User user) {
        PublishedDocument publishedDocument = new PublishedDocument();

        PublishedDocumentMetaData publishedMetaData = constructPublishedDocumentMetaData(document.getMetaData());
        publishedDocument.setPublishedDocumentMetaData(publishedMetaData);

        publishedDocument.setCreatedAt(ZonedDateTime.now());
        publishedDocument.setUser(user);
        publishedDocument.setViews(0);
        publishedDocument.setStatus(PublishedDocumentStatus.IN_PROGRESS);
        return publishedDocument;
    }

    public List<PublishedDocumentData> constructPublishedDocumentDatas(List<PublishedDocument> publishedDocuments) {
        List<PublishedDocumentData> publishedDocumentDatas = new ArrayList<>();
        for (PublishedDocument publishedDocument : publishedDocuments) {
            PublishedDocumentData publishedDocumentData = constructPublishedDocumentData(publishedDocument);
            publishedDocumentDatas.add(publishedDocumentData);
        }
        return publishedDocumentDatas;
    }

    public PublishedDocumentData constructPublishedDocumentData(PublishedDocument publishedDocument) {
        Date createdAt = Date.from(publishedDocument.getCreatedAt().toInstant());
        String publishedDocumentUrl = constructPublishedDocumentUrl(publishedDocument.getHash());
        PublishedDocumentMetaDataData metaData = constructPublishedDocumentMetaDataData(publishedDocument.getPublishedDocumentMetaData());

        PublishedDocumentData publishedDocumentData = new PublishedDocumentData.PublishedDocumentDataBuilder().setHash(publishedDocument.getHash()).setMetaData(metaData)
                .setCreatedAt(createdAt)
                .setUrl(publishedDocumentUrl).setPdfUrl(publishedDocument.getPdfUrl()).setThumbnailUrl(publishedDocument.getThumbnailUrl())
                .setUserName(loadUserName(publishedDocument.getUser())).setViews(publishedDocument.getViews()).setIpfsContentId(publishedDocument.getIpfsContentId()).build();

        setOcrDatas(publishedDocument, publishedDocumentData);

        return publishedDocumentData;
    }

    public String constructPublishedDocumentUrl(String documentHash) {
        String url = MessageFormat.format(PUBLISHED_DOCUMENT_URL, baseUrl, documentHash);
        return url;
    }

    public void constructPublishedDocumentOcrs(Document document, PublishedDocument publishedDocument) {
        List<PublishedDocumentOcr> ocrs = new ArrayList<>();

        List<DocumentImage> images = document.getImages();
        for (DocumentImage image : images) {
            if (StringUtils.hasText(image.getOriginalOcr())) {
                PublishedDocumentOcr ocr = new PublishedDocumentOcr();
                ocr.setPublishedDocument(publishedDocument);
                ocr.setIndex(image.getIndex());
                ocr.setOriginalOcr(image.getOriginalOcr());
                ocr.setEditedOcr(image.getEditedOcr());

                ocrs.add(ocr);
            }
        }

        publishedDocument.setPublishedDocumentOcrs(ocrs);
    }

    private PublishedDocumentMetaData constructPublishedDocumentMetaData(DocumentMetaData metaData) {
        PublishedDocumentMetaData publishedMetaData = new PublishedDocumentMetaData();
        publishedMetaData.setOriginalTitle(metaData.getOriginalTitle());
        publishedMetaData.setOriginalAuthor(metaData.getOriginalAuthor());
        publishedMetaData.setCreatedAtYear(metaData.getCreatedAtYear());
        publishedMetaData.setPeriodCoveredFrom(metaData.getPeriodCoveredFrom());
        publishedMetaData.setPeriodCoveredTo(metaData.getPeriodCoveredTo());
        publishedMetaData.setArchiveName(metaData.getArchiveName());
        publishedMetaData.setArchiveCategory(metaData.getArchiveCategory());
        publishedMetaData.setPublication(metaData.getPublication());
        publishedMetaData.setCatalogUrl(metaData.getCatalogUrl());
        publishedMetaData.setSourceUrl(metaData.getSourceUrl());
        publishedMetaData.setReferenceCode(metaData.getReferenceCode());

        List<DocumentTag> tags = new ArrayList<DocumentTag>(metaData.getTags());
        publishedMetaData.setTags(tags);

        List<DocumentFilter> filters = new ArrayList<DocumentFilter>(metaData.getDocumentFilters());
        publishedMetaData.setDocumentFilters(filters);

        return publishedMetaData;
    }

    private void setOcrDatas(PublishedDocument publishedDocument, PublishedDocumentData publishedDocumentData) {
        List<String> originalOcrs = new ArrayList<>();
        List<String> editedOcrs = new ArrayList<>();

        List<PublishedDocumentOcr> ocrs = publishedDocument.getPublishedDocumentOcrs();
        for (PublishedDocumentOcr ocr : ocrs) {
            originalOcrs.add(ocr.getOriginalOcr());
            if (StringUtils.hasText(ocr.getEditedOcr())) {
                editedOcrs.add(ocr.getEditedOcr());
            }
        }

        publishedDocumentData.setOriginalOcrs(originalOcrs);
        publishedDocumentData.setEditedOcrs(editedOcrs);
    }

    private PublishedDocumentMetaDataData constructPublishedDocumentMetaDataData(PublishedDocumentMetaData publishedDocumentMetaData) {
        List<String> tags = documentTagDataService.collectTagNames(publishedDocumentMetaData.getTags());
        PublishedDocumentMetaDataData metaData = new PublishedDocumentMetaDataData.PublishedDocumentMetaDataDataBuilder()
                .setOriginalTitle(publishedDocumentMetaData.getOriginalTitle()).setOriginalAuthor(publishedDocumentMetaData.getOriginalAuthor())
                .setCreatedAtYear(publishedDocumentMetaData.getCreatedAtYear())
                .setPeriodCoveredFrom(publishedDocumentMetaData.getPeriodCoveredFrom()).setPeriodCoveredTo(publishedDocumentMetaData.getPeriodCoveredTo())
                .setArchiveName(publishedDocumentMetaData.getArchiveName()).setArchiveCategory(publishedDocumentMetaData.getArchiveCategory())
                .setPublication(publishedDocumentMetaData.getPublication())
                .setCatalogUrl(publishedDocumentMetaData.getCatalogUrl()).setSourceUrl(publishedDocumentMetaData.getSourceUrl())
                .setReferenceCode(publishedDocumentMetaData.getReferenceCode()).setTags(tags).build();

        documentFilterTypeService.setPublishedDocumentFilterTypeDatas(publishedDocumentMetaData, metaData);
        return metaData;
    }

    private String loadUserName(User user) {
        String userName = user.getUsername();
        if (!user.isPublicEmail()) {
            String displayName = user.getDisplayName();
            userName = displayName.length() <= USER_DISPLAY_NAME_LIMIT ? displayName : displayName.substring(0, USER_DISPLAY_NAME_LIMIT);
        }
        return userName;
    }

}
