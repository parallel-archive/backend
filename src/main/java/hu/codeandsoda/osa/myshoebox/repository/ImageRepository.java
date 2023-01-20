package hu.codeandsoda.osa.myshoebox.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import hu.codeandsoda.osa.myshoebox.domain.Image;

@Repository
public interface ImageRepository extends CrudRepository<Image, Long> {

    boolean existsByIdAndMyShoeBoxId(Long imageId, Long myShoeBoxId);

    List<Image> findAllByMyShoeBoxId(Long myShoeBoxId);

    int countByMyShoeBoxId(Long myShoeBoxId);

}
