package hu.codeandsoda.osa.documentfilter.service;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import hu.codeandsoda.osa.document.data.DocumentMetaDataRequestData;
import hu.codeandsoda.osa.documentfilter.data.DocumentFilterData;
import hu.codeandsoda.osa.documentfilter.data.DocumentFilterName;
import hu.codeandsoda.osa.documentfilter.data.DocumentFilterTypeName;
import hu.codeandsoda.osa.documentfilter.data.PeriodFilterData;
import hu.codeandsoda.osa.documentfilter.domain.DocumentFilter;
import hu.codeandsoda.osa.documentfilter.repository.DocumentFilterRepository;
import hu.codeandsoda.osa.documentpublish.data.PublicArchivePageFilteringRequest;

@Service
public class DocumentFilterService {

    @Autowired
    private DocumentFilterRepository documentFilterRepository;

    public List<DocumentFilter> loadAllById(List<Long> activeFilterIds) {
        return documentFilterRepository.findAllById(activeFilterIds);
    }

    public void collectAndValidateFilters(PublicArchivePageFilteringRequest publicArchivePageFilteringRequest, List<DocumentFilterName> typeFilterNames, List<DocumentFilterName> languageFilterNames,
            List<DocumentFilterName> countryFilterNames, Integer periodFrom, Integer periodTo) {
        
        Map<DocumentFilterTypeName, List<DocumentFilter>> activeFilters = loadActiveFilters(typeFilterNames, languageFilterNames, countryFilterNames);
        publicArchivePageFilteringRequest.setActiveFilters(activeFilters);
        
        PeriodFilterData periodFilter = loadAndValidatePeriodFilter(periodFrom, periodTo);
        publicArchivePageFilteringRequest.setPeriodFilter(periodFilter);
    }

    public List<Long> collectSelectedFilterIds(List<DocumentFilterName> type, List<DocumentFilterName> language, List<DocumentFilterName> country) {
        List<DocumentFilterName> selectedFilterNames = new ArrayList<>();
        selectedFilterNames.addAll(null != type ? type : Collections.emptyList());
        selectedFilterNames.addAll(null != language ? language : Collections.emptyList());
        selectedFilterNames.addAll(null != country ? country : Collections.emptyList());
        
        List<Long> filterIds = loadAllIdByName(selectedFilterNames);
        return filterIds;
    }

    public List<Long> loadAllIdByName(List<DocumentFilterName> filterNames) {
        List<DocumentFilter> filters = documentFilterRepository.findAllByNameIn(filterNames);
        List<Long> filterIds = filters.stream().map(f -> f.getId()).collect(Collectors.toList());
        return filterIds;
    }

    public List<DocumentFilterData> constructDocumentFilterDatas(List<DocumentFilter> documentFilters, List<Long> activeFilterIds) {
        List<DocumentFilterData> filterDatas = new ArrayList<>();
        for (DocumentFilter documentFilter : documentFilters) {
            DocumentFilterData filterData = constructDocumentFilterData(documentFilter, activeFilterIds);
            filterDatas.add(filterData);
        }
        return filterDatas;
    }

    public List<Long> collectFilterIdsFromFilters(List<DocumentFilter> filters) {
        List<Long> activeFilterIds = filters.stream().map(f -> f.getId()).collect(Collectors.toList());
        return activeFilterIds;
    }

    public List<DocumentFilter> collectActiveDocumentFilters(DocumentMetaDataRequestData documentMetaDataRequestData) {
        List<Long> activeFilterIds = collectFilterIdsFromMetaData(documentMetaDataRequestData);
        List<DocumentFilter> documentFilters = loadAllById(activeFilterIds);
        return documentFilters;
    }

    public List<String> collectFilterNamesByType(List<DocumentFilter> documentFilters, DocumentFilterTypeName type) {
        List<String> filterNames = documentFilters.stream().filter(f -> type.equals(f.getDocumentFilterType().getName())).map(f -> f.getName().getDisplayName())
                .collect(Collectors.toList());
        return filterNames;
    }

    private Map<DocumentFilterTypeName, List<DocumentFilter>> loadActiveFilters(List<DocumentFilterName> typeFilterNames, List<DocumentFilterName> languageFilterNames,
            List<DocumentFilterName> countryFilterNames) {
        Map<DocumentFilterTypeName, List<DocumentFilter>> activeFilters = new HashMap<>();

        List<DocumentFilter> typeFilters = null != typeFilterNames ? loadAllByName(typeFilterNames) : Collections.emptyList();
        activeFilters.put(DocumentFilterTypeName.TYPE, typeFilters);

        List<DocumentFilter> languageFilters = null != languageFilterNames ? loadAllByName(languageFilterNames) : Collections.emptyList();
        activeFilters.put(DocumentFilterTypeName.LANGUAGE, languageFilters);

        List<DocumentFilter> countryFilters = null != countryFilterNames ? loadAllByName(countryFilterNames) : Collections.emptyList();
        activeFilters.put(DocumentFilterTypeName.COUNTRY, countryFilters);

        return activeFilters;
    }

    private PeriodFilterData loadAndValidatePeriodFilter(Integer periodFrom, Integer periodTo) {
        periodFrom = validatePeriodFrom(periodFrom);
        periodTo = validatePeriodTo(periodTo);
        PeriodFilterData periodFilter = new PeriodFilterData.PeriodFilterDataBuilder().setFrom(periodFrom).setTo(periodTo).build();
        return periodFilter;
    }

    private Integer validatePeriodFrom(Integer periodFrom) {
        if (null != periodFrom && periodFrom < 0) {
            periodFrom = 0;
        }
        return periodFrom;
    }

    private Integer validatePeriodTo(Integer periodTo) {
        int currentYear = ZonedDateTime.now().getYear();
        if (null != periodTo && periodTo > currentYear) {
            periodTo = currentYear;
        }
        return periodTo;
    }

    private List<DocumentFilter> loadAllByName(List<DocumentFilterName> filterNames) {
        List<DocumentFilter> filters = documentFilterRepository.findAllByNameIn(filterNames);
        return filters;
    }

    private DocumentFilterData constructDocumentFilterData(DocumentFilter documentFilter, List<Long> activeFilterIds) {
        Long id = documentFilter.getId();
        boolean active = activeFilterIds.contains(id);
        DocumentFilterData filterData = new DocumentFilterData.DocumentFilterDataBuilder().setId(id).setName(documentFilter.getName()).setActive(active).build();
        return filterData;
    }

    private List<Long> collectFilterIdsFromMetaData(DocumentMetaDataRequestData documentMetaDataRequestData) {
        List<Long> filterIds = documentMetaDataRequestData.getTypes();
        filterIds.addAll(documentMetaDataRequestData.getLanguages());
        filterIds.addAll(documentMetaDataRequestData.getCountriesCovered());
        return filterIds;
    }

}
