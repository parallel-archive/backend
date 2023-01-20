package hu.codeandsoda.osa.document.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import hu.codeandsoda.osa.account.user.domain.User;
import hu.codeandsoda.osa.document.domain.Document;
import hu.codeandsoda.osa.documentpublish.domain.PublishedDocument;

@Repository
public interface DocumentRepository extends CrudRepository<Document, Long> {

    boolean existsByIdAndUserId(Long imageId, Long userId);

    List<Document> findAllByUserAndPublishedDocumentIsNullOrderByModifiedAtDesc(User user);

    List<Document> findAllByUser(User user);

    Document findByPublishedDocument(PublishedDocument publishedDocument);

    int countByUserIdAndPublishedDocumentIsNull(Long userId);

}
