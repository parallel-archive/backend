package hu.codeandsoda.osa.document.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import hu.codeandsoda.osa.document.domain.DocumentImage;

@Repository
public interface DocumentImageRepository extends CrudRepository<DocumentImage, Long> {

    boolean existsByIdAndDocumentId(Long id, Long documentId);

    List<DocumentImage> findAllByDocumentIdOrderByIndex(Long documentId);

}
