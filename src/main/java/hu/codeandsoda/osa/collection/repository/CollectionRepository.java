package hu.codeandsoda.osa.collection.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import hu.codeandsoda.osa.account.user.domain.User;
import hu.codeandsoda.osa.collection.domain.Collection;

@Repository
public interface CollectionRepository extends CrudRepository<Collection, Long> {

    boolean existsByNameAndUserId(String name, Long userId);

    List<Collection> findAllByUserId(Long userId);

    boolean existsByIdAndUserId(Long collectionId, Long userId);

    void deleteAllByUser(User user);

}
