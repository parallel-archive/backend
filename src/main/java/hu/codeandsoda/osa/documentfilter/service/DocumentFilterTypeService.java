package hu.codeandsoda.osa.documentfilter.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import hu.codeandsoda.osa.document.data.DocumentMetaDataData;
import hu.codeandsoda.osa.document.domain.DocumentMetaData;
import hu.codeandsoda.osa.document.domain.DocumentTag;
import hu.codeandsoda.osa.document.service.DocumentTagDataService;
import hu.codeandsoda.osa.documentfilter.data.DocumentFilterData;
import hu.codeandsoda.osa.documentfilter.data.DocumentFilterTypeData;
import hu.codeandsoda.osa.documentfilter.data.DocumentFilterTypeName;
import hu.codeandsoda.osa.documentfilter.data.DocumentFilterTypesData;
import hu.codeandsoda.osa.documentfilter.domain.DocumentFilter;
import hu.codeandsoda.osa.documentfilter.domain.DocumentFilterType;
import hu.codeandsoda.osa.documentfilter.repository.DocumentFilterTypeRepository;
import hu.codeandsoda.osa.documentpublish.data.PublicArchivePageFilteringRequest;
import hu.codeandsoda.osa.documentpublish.data.PublishedDocumentMetaDataData;
import hu.codeandsoda.osa.documentpublish.domain.PublishedDocument;
import hu.codeandsoda.osa.documentpublish.domain.PublishedDocumentMetaData;

@Service
public class DocumentFilterTypeService {

    @Autowired
    private DocumentFilterService documentFilterService;

    @Autowired
    private DocumentTagDataService documentTagDataService;

    @Autowired
    private DocumentFilterTypeRepository documentFilterTypeRepository;

    public DocumentFilterType loadByName(DocumentFilterTypeName name) {
        return documentFilterTypeRepository.findByName(name);
    }

    public DocumentFilterTypesData loadFilterTypesData(PublicArchivePageFilteringRequest publicArchivePageFilteringRequest, List<PublishedDocument> filteredPublishedDocuments) {
        List<DocumentFilterTypeData> filterTypeDatas = constructFilterTypeDatas(publicArchivePageFilteringRequest, filteredPublishedDocuments);

        Set<String> tags = loadPublishedDocumentTags(filteredPublishedDocuments, publicArchivePageFilteringRequest.getActiveTags());

        DocumentFilterTypesData filterTypesData = new DocumentFilterTypesData.DocumentFilterTypesDataBuilder().setFilterTypes(filterTypeDatas)
                .setPeriodFilter(publicArchivePageFilteringRequest.getPeriodFilter())
                .setTags(tags).build();
        return filterTypesData;
    }

    public void setDocumentFilterTypeDatas(DocumentMetaData metaData, DocumentMetaDataData documentMetaDataData) {
        List<Long> activeFilterIds = documentFilterService.collectFilterIdsFromFilters(metaData.getDocumentFilters());
        documentMetaDataData.setTypes(loadDocumentFilterTypeData(DocumentFilterTypeName.TYPE, activeFilterIds));
        documentMetaDataData.setLanguages(loadDocumentFilterTypeData(DocumentFilterTypeName.LANGUAGE, activeFilterIds));
        documentMetaDataData.setCountriesCovered(loadDocumentFilterTypeData(DocumentFilterTypeName.COUNTRY, activeFilterIds));
    }

    public void setPublishedDocumentFilterTypeDatas(PublishedDocumentMetaData publishedDocumentMetaData, PublishedDocumentMetaDataData publishedDocumentMetaDataData) {
        Map<DocumentFilterTypeName, List<String>> filtersByType = collectFiltersByType(publishedDocumentMetaData.getDocumentFilters());
        
        publishedDocumentMetaDataData.setTypes(loadFiltersByType(filtersByType, DocumentFilterTypeName.TYPE));
        publishedDocumentMetaDataData
                .setLanguages(loadFiltersByType(filtersByType, DocumentFilterTypeName.LANGUAGE));
        publishedDocumentMetaDataData
                .setCountriesCovered(loadFiltersByType(filtersByType, DocumentFilterTypeName.COUNTRY));
    }

    private List<DocumentFilterTypeData> constructFilterTypeDatas(PublicArchivePageFilteringRequest publicArchivePageFilteringRequest,
            List<PublishedDocument> publishedDocuments) {
        Map<DocumentFilterType, Set<DocumentFilter>> activeFilters = new HashMap<>();
        Map<DocumentFilterType, Set<DocumentFilter>> inactiveFilters = new HashMap<>();

        Map<DocumentFilterTypeName, List<DocumentFilter>> activeFiltersByType = publicArchivePageFilteringRequest.getActiveFilters();

        for (PublishedDocument publishedDocument : publishedDocuments) {
            for (DocumentFilter filter : publishedDocument.getPublishedDocumentMetaData().getDocumentFilters()) {

                DocumentFilterType filterType = filter.getDocumentFilterType();

                if (activeFiltersByType.containsKey(filterType.getName()) && activeFiltersByType.get(filterType.getName()).contains(filter)) {
                    addFilterToMap(activeFilters, filter, filterType);
                } else {
                    addFilterToMap(inactiveFilters, filter, filterType);
                }
            }
        }

        DocumentFilterTypeData typeFilter = loadFilterTypeData(DocumentFilterTypeName.TYPE, activeFilters, inactiveFilters);
        DocumentFilterTypeData languageFilter = loadFilterTypeData(DocumentFilterTypeName.LANGUAGE, activeFilters, inactiveFilters);
        DocumentFilterTypeData countryFilter = loadFilterTypeData(DocumentFilterTypeName.COUNTRY, activeFilters, inactiveFilters);

        List<DocumentFilterTypeData> filterTypeDatas = Arrays.asList(typeFilter, languageFilter, countryFilter);
        return filterTypeDatas;
    }

    private void addFilterToMap(Map<DocumentFilterType, Set<DocumentFilter>> filtersByName, DocumentFilter filter, DocumentFilterType filterType) {
        Set<DocumentFilter> filters = filtersByName.containsKey(filterType) ? filtersByName.get(filterType) : new HashSet<>();
        filters.add(filter);

        filtersByName.put(filterType, filters);
    }

    private DocumentFilterTypeData loadFilterTypeData(DocumentFilterTypeName filterTypeName, Map<DocumentFilterType, Set<DocumentFilter>> activeFilters,
            Map<DocumentFilterType, Set<DocumentFilter>> inactiveFilters) {

        List<DocumentFilterData> filterDatas = new ArrayList<>();

        List<DocumentFilterData> activeFilterDatas = collectFilterDatas(activeFilters, filterTypeName, true);
        filterDatas.addAll(activeFilterDatas);

        List<DocumentFilterData> inactiveFilterDatas = collectFilterDatas(inactiveFilters, filterTypeName, false);
        filterDatas.addAll(inactiveFilterDatas);

        Long filterTypeId = loadFilterTypeId(activeFilters, inactiveFilters, filterTypeName);

        DocumentFilterTypeData filterTypeData = new DocumentFilterTypeData.DocumentFilterTypeDataBuilder().setId(filterTypeId).setName(filterTypeName).setFilters(filterDatas)
                .build();

        return filterTypeData;
    }

    private List<DocumentFilterData> collectFilterDatas(Map<DocumentFilterType, Set<DocumentFilter>> filters, DocumentFilterTypeName filterTypeName, boolean isFilterActive) {
        List<DocumentFilterData> filterDatas = new ArrayList<>();

        for (Entry<DocumentFilterType, Set<DocumentFilter>> filterEntry : filters.entrySet()) {
            if (filterTypeName == filterEntry.getKey().getName()) {
                Set<DocumentFilter> filtersOfType = filterEntry.getValue();
                for (DocumentFilter filter : filtersOfType) {
                    DocumentFilterData filterData = new DocumentFilterData.DocumentFilterDataBuilder().setId(filter.getId()).setActive(isFilterActive).setName(filter.getName())
                            .build();
                    filterDatas.add(filterData);
                }
                break;
            }
        }

        Comparator<DocumentFilterData> nameComparator = loadFilterDataNameComparator();
        Collections.sort(filterDatas, nameComparator);

        return filterDatas;
    }

    private Set<String> loadPublishedDocumentTags(List<PublishedDocument> filteredPublishedDocuments, List<DocumentTag> activeTags) {
        Set<String> tags = new HashSet<>();
        if (!activeTags.isEmpty()) {
            tags = documentTagDataService.collectInactiveTagNamesFromDocuments(filteredPublishedDocuments, activeTags);
        }
        return tags;
    }

    private Long loadFilterTypeId(Map<DocumentFilterType, Set<DocumentFilter>> activeFilters, Map<DocumentFilterType, Set<DocumentFilter>> inactiveFilters,
            DocumentFilterTypeName filterTypeName) {
        Long filterTypeId = null;
        if (!activeFilters.isEmpty()) {
            for (DocumentFilterType filterType : activeFilters.keySet()) {
                if (filterTypeName == filterType.getName()) {
                    filterTypeId = filterType.getId();
                    break;
                }
            }
        }

        if (null == filterTypeId && !inactiveFilters.isEmpty()) {
            for (DocumentFilterType filterType : inactiveFilters.keySet()) {
                if (filterTypeName == filterType.getName()) {
                    filterTypeId = filterType.getId();
                    break;
                }
            }
        }
        return filterTypeId;
    }

    private DocumentFilterTypeData constructFilterTypeData(DocumentFilterType filterType, List<Long> activeFilterIds) {
        List<DocumentFilterData> filterDatas = documentFilterService.constructDocumentFilterDatas(filterType.getDocumentFilters(), activeFilterIds);
        DocumentFilterTypeData filterTypeData = new DocumentFilterTypeData.DocumentFilterTypeDataBuilder().setId(filterType.getId()).setName(filterType.getName())
                .setFilters(filterDatas).build();
        return filterTypeData;
    }

    private DocumentFilterTypeData loadDocumentFilterTypeData(DocumentFilterTypeName type, List<Long> activeFilterIds) {
        DocumentFilterType filterType = loadByName(type);
        DocumentFilterTypeData filterTypeData = constructFilterTypeData(filterType, activeFilterIds);
        return filterTypeData;
    }

    private Map<DocumentFilterTypeName, List<String>> collectFiltersByType(List<DocumentFilter> documentFilters) {
        Map<DocumentFilterTypeName, List<String>> filtersByType = new HashMap<>();
        for (DocumentFilter documentFilter : documentFilters) {
            DocumentFilterTypeName filterType = documentFilter.getDocumentFilterType().getName();

            filtersByType.computeIfAbsent(filterType, v -> new ArrayList<>());
            List<String> filters = filtersByType.get(filterType);
            filters.add(documentFilter.getName().getDisplayName());
        }
        return filtersByType;
    }

    private List<String> loadFiltersByType(Map<DocumentFilterTypeName, List<String>> filtersByType, DocumentFilterTypeName type) {
        List<String> filters = filtersByType.containsKey(type) ? filtersByType.get(type) : Collections.emptyList();
        return filters;
    }

    private Comparator<DocumentFilterData> loadFilterDataNameComparator() {
        Comparator<DocumentFilterData> nameComparator = (DocumentFilterData d1, DocumentFilterData d2) -> d1.getName().getDisplayName().compareTo(d2.getName().getDisplayName());
        return nameComparator;

    }
}
