package hu.codeandsoda.osa.myshoebox.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import hu.codeandsoda.osa.myshoebox.domain.MyShoeBox;

@Repository
public interface MyShoeBoxRepository extends CrudRepository<MyShoeBox, Long> {

}
