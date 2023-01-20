package hu.codeandsoda.osa.documentpublish.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import hu.codeandsoda.osa.account.user.domain.User;
import hu.codeandsoda.osa.documentpublish.domain.PublishedDocumentAnnotation;

@Repository
public interface PublishedDocumentAnnotationRepository extends CrudRepository<PublishedDocumentAnnotation, Long> {

    PublishedDocumentAnnotation findByPublishedDocumentIdAndUserId(Long publishedDocumentId, Long userId);

    List<PublishedDocumentAnnotation> findAllByUserId(Long userId);

    void deleteAllByUser(User user);

    boolean existsByPublishedDocumentIdAndUserId(Long publishedDocumentId, Long userId);

}
