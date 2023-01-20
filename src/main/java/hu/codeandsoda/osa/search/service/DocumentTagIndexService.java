package hu.codeandsoda.osa.search.service;

import java.util.ArrayList;
import java.util.List;

import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.data.elasticsearch.core.query.IndexQueryBuilder;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Service;

import hu.codeandsoda.osa.document.domain.DocumentTag;
import hu.codeandsoda.osa.search.data.DocumentTagSearchResult;
import hu.codeandsoda.osa.search.index.DocumentTagIndex;

@Service
public class DocumentTagIndexService {

    private static final String DOCUMENT_TAG_INDEX = "document-tag";

    @Autowired
    private ElasticsearchOperations elasticsearchOperations;

    public void indexDocumentTag(DocumentTag tag) {
        String tagIndexId = tag.getId().toString();
        DocumentTagIndex tagIndex = new DocumentTagIndex(tagIndexId, tag.getName());
        IndexQuery indexQuery = new IndexQueryBuilder().withId(tagIndexId).withObject(tagIndex).build();

        elasticsearchOperations.index(indexQuery, IndexCoordinates.of(DOCUMENT_TAG_INDEX));
    }

    public DocumentTagSearchResult autoSuggest(String searchTerm) {
        QueryBuilder queryBuilder = QueryBuilders.matchQuery("name", searchTerm).analyzer("standard").operator(Operator.AND);
        Query searchQuery = new NativeSearchQueryBuilder().withQuery(queryBuilder).build();
        SearchHits<DocumentTagIndex> searchHits = elasticsearchOperations.search(searchQuery, DocumentTagIndex.class, IndexCoordinates.of(DOCUMENT_TAG_INDEX));

        DocumentTagSearchResult documentTagsData = constructDocumentTagsData(searchHits);
        return documentTagsData;
    }

    private DocumentTagSearchResult constructDocumentTagsData(SearchHits<DocumentTagIndex> searchHits) {
        List<String> tags = new ArrayList<>();
        if (searchHits.hasSearchHits()) {
            List<SearchHit<DocumentTagIndex>> searchResults = searchHits.getSearchHits();
            for (SearchHit<DocumentTagIndex> searchHit : searchResults) {
                DocumentTagIndex tagIndex = searchHit.getContent();
                tags.add(tagIndex.getName());
            }
        }
        DocumentTagSearchResult documentTagsData = new DocumentTagSearchResult.DocumentTagsDataBuilder().setTags(tags).build();
        return documentTagsData;
    }

}
