package hu.codeandsoda.osa.documentfilter.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import hu.codeandsoda.osa.documentfilter.data.DocumentFilterName;
import hu.codeandsoda.osa.documentfilter.domain.DocumentFilter;

@Repository
public interface DocumentFilterRepository extends CrudRepository<DocumentFilter, Long> {

    @Override
    List<DocumentFilter> findAllById(Iterable<Long> ids);

    List<DocumentFilter> findAllByNameIn(List<DocumentFilterName> filterNames);

}
