package hu.codeandsoda.osa.documentfilter.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import hu.codeandsoda.osa.documentfilter.data.DocumentFilterTypeName;
import hu.codeandsoda.osa.documentfilter.domain.DocumentFilterType;

@Repository
public interface DocumentFilterTypeRepository extends CrudRepository<DocumentFilterType, Long> {

    DocumentFilterType findByName(DocumentFilterTypeName name);

    @Override
    List<DocumentFilterType> findAll();

}
