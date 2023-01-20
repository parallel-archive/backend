package hu.codeandsoda.osa.account.user.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import hu.codeandsoda.osa.account.user.domain.User;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {

    User findByEmail(String email);

    @Override
    User save(User user);

    User findByMyShoeBoxId(Long myShoeBoxId);

}
