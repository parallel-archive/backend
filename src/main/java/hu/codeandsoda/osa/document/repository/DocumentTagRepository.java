package hu.codeandsoda.osa.document.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import hu.codeandsoda.osa.document.domain.DocumentTag;

@Repository
public interface DocumentTagRepository extends CrudRepository<DocumentTag, Long> {

    DocumentTag findByName(String name);

    boolean existsByName(String name);

}
