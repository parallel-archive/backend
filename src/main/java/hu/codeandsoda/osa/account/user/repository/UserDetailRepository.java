package hu.codeandsoda.osa.account.user.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import hu.codeandsoda.osa.account.user.domain.UserDetail;

@Repository
public interface UserDetailRepository extends CrudRepository<UserDetail, Long> {

}
