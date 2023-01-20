package hu.codeandsoda.osa.resetpassword.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import hu.codeandsoda.osa.account.user.domain.User;
import hu.codeandsoda.osa.resetpassword.domain.PasswordResetToken;

@Repository
public interface PasswordResetTokenRepository extends CrudRepository<PasswordResetToken, Long> {

    PasswordResetToken findByToken(String token);

    boolean existsByToken(String token);

    PasswordResetToken findByUser(User user);

    void deleteByUser(User user);

}
