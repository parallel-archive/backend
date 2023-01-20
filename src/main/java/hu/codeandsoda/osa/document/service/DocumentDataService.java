package hu.codeandsoda.osa.document.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import hu.codeandsoda.osa.account.user.data.UserDocumentData;
import hu.codeandsoda.osa.account.user.data.UserDocumentImageData;
import hu.codeandsoda.osa.account.user.data.UserDocumentMetaDataData;
import hu.codeandsoda.osa.document.data.DocumentData;
import hu.codeandsoda.osa.document.data.DocumentImageData;
import hu.codeandsoda.osa.document.data.DocumentMetaDataData;
import hu.codeandsoda.osa.document.domain.Document;
import hu.codeandsoda.osa.document.domain.DocumentImage;
import hu.codeandsoda.osa.document.domain.DocumentMetaData;
import hu.codeandsoda.osa.documentfilter.data.DocumentFilterTypeName;
import hu.codeandsoda.osa.documentfilter.domain.DocumentFilter;
import hu.codeandsoda.osa.documentfilter.service.DocumentFilterService;
import hu.codeandsoda.osa.documentfilter.service.DocumentFilterTypeService;

@Service
public class DocumentDataService {

    @Autowired
    private DocumentTagDataService documentTagDataService;

    @Autowired
    private DocumentFilterTypeService documentFilterTypeService;

    @Autowired
    private DocumentFilterService documentFilterService;

    public List<DocumentData> constructDocumentDatas(List<Document> documents) {
        List<DocumentData> documentDatas = new ArrayList<>();
        for (Document document : documents) {
            DocumentData documentData = constructDocumentData(document);
            documentDatas.add(documentData);
        }
        return documentDatas;
    }

    public DocumentData constructDocumentData(Document document) {
        DocumentMetaDataData metaData = constructDocumentMetaDataData(document.getMetaData());
        List<DocumentImageData> imageDatas = constructDocumentImageDatas(document.getImages());

        DocumentData documentData = new DocumentData.DocumentDataBuilder().setId(document.getId()).setMetaData(metaData).setImages(imageDatas)
                .setUploadedAt(document.getUploadedAt()).setModifiedAt(document.getModifiedAt()).build();
        return documentData;
    }

    public List<DocumentImageData> constructDocumentImageDatas(List<DocumentImage> images) {
        List<DocumentImageData> imageDatas = new ArrayList<>();
        for (DocumentImage image : images) {
            DocumentImageData imageData = constructDocumentImageData(image);
            imageDatas.add(imageData);
        }
        return imageDatas;
    }

    private DocumentMetaDataData constructDocumentMetaDataData(DocumentMetaData metaData) {
        List<String> tagNames = documentTagDataService.collectTagNames(metaData.getTags());
        DocumentMetaDataData documentMetaDataData = new DocumentMetaDataData.DocumentMetaDataDataBuilder().setOriginalTitle(metaData.getOriginalTitle())
                .setOriginalAuthor(metaData.getOriginalAuthor())
                .setCreatedAtYear(metaData.getCreatedAtYear()).setPeriodCoveredFrom(metaData.getPeriodCoveredFrom()).setPeriodCoveredTo(metaData.getPeriodCoveredTo())
                .setArchiveName(metaData.getArchiveName()).setArchiveCategory(metaData.getArchiveCategory()).setPublication(metaData.getPublication())
                .setCatalogUrl(metaData.getCatalogUrl()).setSourceUrl(metaData.getSourceUrl()).setReferenceCode(metaData.getReferenceCode()).setTags(tagNames).build();

        documentFilterTypeService.setDocumentFilterTypeDatas(metaData, documentMetaDataData);
        return documentMetaDataData;
    }

    private DocumentImageData constructDocumentImageData(DocumentImage image) {
        DocumentImageData imageData = new DocumentImageData.DocumentImageDataBuilder().setId(image.getId()).setName(image.getName()).setIndex(image.getIndex())
                .setUrl(image.getUrl()).setThumbnailUrl(image.getThumbnailUrl()).setUploadedAt(image.getUploadedAt()).setOcr(image.getEditedOcr()).build();
        return imageData;
    }

    public List<UserDocumentData> constructUserDocumentDatas(List<Document> documents) {
        List<UserDocumentData> documentDatas = new ArrayList<>();
        for (Document document : documents) {
            UserDocumentData documentData = constructUserDocumentData(document);
            documentDatas.add(documentData);
        }
        return documentDatas;
    }

    private UserDocumentData constructUserDocumentData(Document document) {
        UserDocumentMetaDataData metaData = constructUserDocumentMetaDataData(document.getMetaData());
        List<UserDocumentImageData> images = constructUserDocumentImageDatas(document.getImages());
        UserDocumentData documentData = new UserDocumentData.UserDocumentDataBuilder().setMetaData(metaData).setImages(images).setUploadedAt(document.getUploadedAt())
                .setModifiedAt(document.getModifiedAt()).build();
        return documentData;
    }

    private UserDocumentMetaDataData constructUserDocumentMetaDataData(DocumentMetaData metaData) {
        List<DocumentFilter> documentFilters = metaData.getDocumentFilters();
        List<String> types = documentFilterService.collectFilterNamesByType(documentFilters, DocumentFilterTypeName.TYPE);
        List<String> languages = documentFilterService.collectFilterNamesByType(documentFilters, DocumentFilterTypeName.LANGUAGE);
        List<String> countriesCovered = documentFilterService.collectFilterNamesByType(documentFilters, DocumentFilterTypeName.COUNTRY);
        
        List<String> tags = documentTagDataService.collectTagNames(metaData.getTags());
        
        UserDocumentMetaDataData metaDataData = new UserDocumentMetaDataData.UserDocumentMetaDataDataBuilder().setOriginalTitle(metaData.getOriginalTitle())
                .setOriginalAuthor(metaData.getOriginalAuthor())
                .setCreatedAtYear(metaData.getCreatedAtYear()).setTypes(types).setLanguages(languages).setPeriodCoveredFrom(metaData.getPeriodCoveredFrom())
                .setPeriodCoveredTo(metaData.getPeriodCoveredTo()).setCountriesCovered(countriesCovered).setArchiveName(metaData.getArchiveName())
                .setArchiveCategory(metaData.getArchiveCategory()).setPublication(metaData.getPublication()).setCatalogUrl(metaData.getCatalogUrl())
                .setSourceUrl(metaData.getSourceUrl()).setReferenceCode(metaData.getReferenceCode()).setTags(tags).build();
        return metaDataData;
    }

    private List<UserDocumentImageData> constructUserDocumentImageDatas(List<DocumentImage> images) {
        List<UserDocumentImageData> imageDatas = new ArrayList<>();
        for (DocumentImage image : images) {
            UserDocumentImageData imageData = constructUserDocumentImageData(image);
            imageDatas.add(imageData);
        }
        return imageDatas;
    }

    private UserDocumentImageData constructUserDocumentImageData(DocumentImage image) {
        UserDocumentImageData imageData = new UserDocumentImageData.UserDocumentImageDataBuilder().setName(image.getName()).setIndex(image.getIndex()).setUrl(image.getUrl())
                .setThumbnailUrl(image.getThumbnailUrl()).setUploadedAt(image.getUploadedAt()).setOriginalOcr(image.getOriginalOcr()).setEditedOcr(image.getEditedOcr()).build();
        return imageData;
    }

}
