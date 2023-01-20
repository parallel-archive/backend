package hu.codeandsoda.osa.search.service;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.data.elasticsearch.core.query.IndexQueryBuilder;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;

import hu.codeandsoda.osa.document.domain.DocumentTag;
import hu.codeandsoda.osa.documentpublish.domain.PublishedDocument;
import hu.codeandsoda.osa.documentpublish.domain.PublishedDocumentMetaData;
import hu.codeandsoda.osa.documentpublish.domain.PublishedDocumentOcr;
import hu.codeandsoda.osa.search.index.PublishedDocumentMetaDataIndex;
import hu.codeandsoda.osa.search.index.PublishedDocumentOcrIndex;
import hu.codeandsoda.osa.search.index.PublishedDocumentTagIndex;

@Service
public class PublishedDocumentMetaDataIndexService {

    private static final String PUBLISHED_DOCUMENT_TAG_INDEX = "published-document-metadata-tag";

    private static Logger logger = LoggerFactory.getLogger(PublishedDocumentMetaDataIndexService.class);


    @Autowired
    private ElasticsearchOperations elasticsearchOperations;


    public void index(PublishedDocument publishedDocument) {
        String logStart = MessageFormat.format("STARTED indexing Published Document, documentId={0}", publishedDocument.getId());
        logger.info(logStart);

        String publishedDocumentIndexId = publishedDocument.getId().toString();
        PublishedDocumentMetaDataIndex publishedDocumentIndex = constructPublishedDocumentIndex(publishedDocumentIndexId, publishedDocument);
        IndexQuery indexQuery = new IndexQueryBuilder().withId(publishedDocumentIndexId).withObject(publishedDocumentIndex).build();

        elasticsearchOperations.index(indexQuery, IndexCoordinates.of(PUBLISHED_DOCUMENT_TAG_INDEX));

        String logEnd = MessageFormat.format("ENDED indexing Published Document, documentId={0}", publishedDocument.getId());
        logger.info(logEnd);
    }

    private PublishedDocumentMetaDataIndex constructPublishedDocumentIndex(String id, PublishedDocument publishedDocument) {
        PublishedDocumentMetaDataIndex documentIndex = new PublishedDocumentMetaDataIndex();
        documentIndex.setId(id);

        PublishedDocumentMetaData metaData = publishedDocument.getPublishedDocumentMetaData();
        documentIndex.setOriginalTitle(metaData.getOriginalTitle());
        documentIndex.setArchiveName(metaData.getArchiveName());
        documentIndex.setArchiveCategory(metaData.getArchiveCategory());
        documentIndex.setPublication(metaData.getPublication());
        documentIndex.setReferenceCode(metaData.getReferenceCode());

        List<PublishedDocumentOcrIndex> publishedDocumentOcrs = collectPublishedDocumentOcrs(publishedDocument.getPublishedDocumentOcrs());
        documentIndex.setEditedOcrs(publishedDocumentOcrs);

        List<PublishedDocumentTagIndex> tags = collectPublishedDocumentTags(metaData.getTags());
        documentIndex.setTags(tags);

        return documentIndex;
    }

    public List<Long> searchPublishedDocument(String searchTerm) {
        QueryBuilder queryBuilder = QueryBuilders.multiMatchQuery(searchTerm).field("originalTitle", 10).field("archiveName", 2).field("archiveCategory", 2)
                .field("publication", 2).field("referenceCode", 2)
                .field("editedOcrs.editedOcr", 1).field("tags.tag", 3).type(MultiMatchQueryBuilder.Type.BEST_FIELDS)
                .operator(Operator.AND);

        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(queryBuilder).build();

        SearchHits<PublishedDocumentMetaDataIndex> searchHits = elasticsearchOperations.search(searchQuery, PublishedDocumentMetaDataIndex.class,
                IndexCoordinates.of(PUBLISHED_DOCUMENT_TAG_INDEX));

        List<Long> documentIds = collectPublishedDocumentIds(searchHits);
        return documentIds;
    }

    private List<PublishedDocumentOcrIndex> collectPublishedDocumentOcrs(List<PublishedDocumentOcr> publishedDocumentOcrs) {
        List<PublishedDocumentOcrIndex> ocrIndexes = new ArrayList<>();
        for (PublishedDocumentOcr publishedDocumentOcr : publishedDocumentOcrs) {
            PublishedDocumentOcrIndex ocrIndex = new PublishedDocumentOcrIndex(publishedDocumentOcr.getEditedOcr());
            ocrIndexes.add(ocrIndex);
        }
        return ocrIndexes;
    }

    private List<PublishedDocumentTagIndex> collectPublishedDocumentTags(List<DocumentTag> tags) {
        List<PublishedDocumentTagIndex> tagIndexes = new ArrayList<>();
        for (DocumentTag tag : tags) {
            PublishedDocumentTagIndex tagIndex = new PublishedDocumentTagIndex(tag.getName());
            tagIndexes.add(tagIndex);
        }
        return tagIndexes;
    }

    private List<Long> collectPublishedDocumentIds(SearchHits<PublishedDocumentMetaDataIndex> searchHits) {
        List<Long> documentIds = new ArrayList<>();
        if (searchHits.hasSearchHits()) {
            documentIds = searchHits.getSearchHits().stream().map(s -> Long.valueOf(s.getContent().getId())).collect(Collectors.toList());
        }
        return documentIds;
    }

}
