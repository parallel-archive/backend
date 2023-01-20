package hu.codeandsoda.osa.search.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import hu.codeandsoda.osa.search.index.DocumentTagIndex;

public interface DocumentTagIndexRepository extends ElasticsearchRepository<DocumentTagIndex, String> {

}
