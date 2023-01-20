package hu.codeandsoda.osa.registration.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import hu.codeandsoda.osa.account.user.domain.User;
import hu.codeandsoda.osa.registration.domain.RegistrationToken;

@Repository
public interface RegistrationTokenRepository extends CrudRepository<RegistrationToken, Long> {

    RegistrationToken findByToken(String token);

    boolean existsByToken(String token);

    void deleteByUser(User user);

}
