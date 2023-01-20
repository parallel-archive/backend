package hu.codeandsoda.osa.ocr.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import hu.codeandsoda.osa.account.user.domain.User;
import hu.codeandsoda.osa.ocr.domain.OcrRateLimit;

@Repository
public interface OcrRateLimitRepository extends CrudRepository<OcrRateLimit, Long> {

    void deleteByUser(User user);

    OcrRateLimit findByUser(User user);

}
