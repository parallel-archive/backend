package hu.codeandsoda.osa.search.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import hu.codeandsoda.osa.search.index.PublishedDocumentMetaDataIndex;

public interface PublishedDocumentMetaDataIndexRepository extends ElasticsearchRepository<PublishedDocumentMetaDataIndex, String> {

}
