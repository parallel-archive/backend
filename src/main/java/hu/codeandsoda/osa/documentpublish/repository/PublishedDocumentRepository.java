package hu.codeandsoda.osa.documentpublish.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import hu.codeandsoda.osa.account.user.domain.User;
import hu.codeandsoda.osa.documentpublish.data.PublishedDocumentStatus;
import hu.codeandsoda.osa.documentpublish.domain.PublishedDocument;

@Repository
public interface PublishedDocumentRepository extends CrudRepository<PublishedDocument, Long> {

    List<PublishedDocument> findByUser(User user);

    List<PublishedDocument> findByUserIdAndStatusOrderByCreatedAtDesc(Long userId, PublishedDocumentStatus status);

    PublishedDocument findByHash(String hash);

    boolean existsByHash(String hash);

    boolean existsByHashAndStatus(String hash, PublishedDocumentStatus status);

    int countByUserIdAndStatus(Long userId, PublishedDocumentStatus status);

    List<PublishedDocument> findByPublishedDocumentMetaDataPeriodCoveredFromGreaterThanEqualAndPublishedDocumentMetaDataPeriodCoveredToLessThanEqualAndStatusOrderByCreatedAtDesc(
            Integer periodCoveredFrom, Integer periodCoveredTo, PublishedDocumentStatus status);

    List<PublishedDocument> findByPublishedDocumentMetaDataPeriodCoveredFromGreaterThanEqualAndStatusOrderByCreatedAtDesc(Integer periodFrom, PublishedDocumentStatus status);

    List<PublishedDocument> findByPublishedDocumentMetaDataPeriodCoveredToLessThanEqualAndStatusOrderByCreatedAtDesc(Integer periodTo, PublishedDocumentStatus status);

    List<PublishedDocument> findByStatusOrderByCreatedAtDesc(PublishedDocumentStatus status);

    List<PublishedDocument> findByPublishedDocumentMetaDataPeriodCoveredFromGreaterThanEqualAndPublishedDocumentMetaDataPeriodCoveredToLessThanEqualAndIdInAndStatusOrderByCreatedAtDesc(
            Integer periodFrom, Integer periodTo, List<Long> searchResultDocumentIds, PublishedDocumentStatus status);

    List<PublishedDocument> findByPublishedDocumentMetaDataPeriodCoveredFromGreaterThanEqualAndIdInAndStatusOrderByCreatedAtDesc(Integer periodFrom,
            List<Long> searchResultDocumentIds, PublishedDocumentStatus status);

    List<PublishedDocument> findByPublishedDocumentMetaDataPeriodCoveredToLessThanEqualAndIdInAndStatusOrderByCreatedAtDesc(Integer periodTo, List<Long> searchResultDocumentIds,
            PublishedDocumentStatus status);

    List<PublishedDocument> findByIdInAndStatusOrderByCreatedAtDesc(List<Long> searchResultDocumentIds, PublishedDocumentStatus status);

    List<PublishedDocument> findFirst20ByPublishedDocumentMetaDataPeriodCoveredFromGreaterThanEqualAndPublishedDocumentMetaDataPeriodCoveredToLessThanEqualAndStatusOrderByCreatedAtDesc(
            Integer periodCoveredFrom, Integer periodCoveredTo, PublishedDocumentStatus status);

    List<PublishedDocument> findFirst20ByPublishedDocumentMetaDataPeriodCoveredFromGreaterThanEqualAndStatusOrderByCreatedAtDesc(Integer periodFrom,
            PublishedDocumentStatus status);

    List<PublishedDocument> findFirst20ByPublishedDocumentMetaDataPeriodCoveredToLessThanEqualAndStatusOrderByCreatedAtDesc(Integer periodTo, PublishedDocumentStatus status);

    List<PublishedDocument> findFirst20ByStatusOrderByCreatedAtDesc(PublishedDocumentStatus status);

    boolean existsByUserIdAndStatus(Long userId, PublishedDocumentStatus status);

    List<PublishedDocument> findByStatus(PublishedDocumentStatus status);







}
