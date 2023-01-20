package hu.codeandsoda.osa.collection.sevice;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import hu.codeandsoda.osa.account.user.data.UserCollectionData;
import hu.codeandsoda.osa.account.user.domain.User;
import hu.codeandsoda.osa.collection.data.CollectionData;
import hu.codeandsoda.osa.collection.data.CollectionRequestData;
import hu.codeandsoda.osa.collection.data.CollectionsData;
import hu.codeandsoda.osa.collection.data.EditCollectionRequestData;
import hu.codeandsoda.osa.collection.domain.Collection;
import hu.codeandsoda.osa.collection.repository.CollectionRepository;
import hu.codeandsoda.osa.documentpublish.data.PublishedDocumentStatus;
import hu.codeandsoda.osa.documentpublish.data.PublishedDocumentsData;
import hu.codeandsoda.osa.documentpublish.domain.PublishedDocument;
import hu.codeandsoda.osa.documentpublish.service.PublishedDocumentService;
import hu.codeandsoda.osa.general.data.ResponseId;
import hu.codeandsoda.osa.general.data.ResponseMessage;
import hu.codeandsoda.osa.general.data.ResponseMessageScope;
import hu.codeandsoda.osa.pagination.data.PaginationRequest;
import hu.codeandsoda.osa.sort.data.SortingRequest;

@Service
public class CollectionService {

    @Autowired
    private CollectionDataService collectionDataService;

    @Autowired
    private PublishedDocumentService publishedDocumentService;

    @Autowired
    private CollectionRepository collectionRepository;

    public CollectionData createCollection(CollectionRequestData collectionRequestData, User user) {
        Collection collection = new Collection();
        collection.setName(collectionRequestData.getName());
        collection.setUser(user);

        addPublishedDocumentIfRequested(collectionRequestData, collection);

        Collection savedCollection = save(collection);
        CollectionData collectionData = collectionDataService.constructCollectionData(savedCollection);
        return collectionData;
    }

    public PublishedDocumentsData loadCollectionDocuments(Collection collection, SortingRequest sortingRequest, PaginationRequest paginationRequest) {
        PublishedDocumentsData publishedDocumentsData = new PublishedDocumentsData();
        if (null != collection) {
            publishedDocumentsData = publishedDocumentService.sortCollectionDocuments(collection.getDocuments(), sortingRequest, paginationRequest);
        } else {
            ResponseMessage message = new ResponseMessage.ResponseMessageBuilder().setId(ResponseId.COLLECTION_URL).setMessage("Invalid collection id.")
                    .setResponseMessageScope(ResponseMessageScope.ERROR).build();
            publishedDocumentsData.setMessages(Arrays.asList(message));
        }
        return publishedDocumentsData;
    }

    public PublishedDocumentsData loadUsersPublishedDocuments(User user, SortingRequest sortingRequest, PaginationRequest paginationRequest) {
        List<PublishedDocument> documents = publishedDocumentService.loadByUserAndStatusOrderByCreatedAtDesc(user.getId(), PublishedDocumentStatus.PUBLISHED);

        PublishedDocumentsData publishedDocumentsData = publishedDocumentService.sortCollectionDocuments(documents, sortingRequest, paginationRequest);
        return publishedDocumentsData;
    }

    public CollectionsData loadCollectionsDataByUser(Long userId) {
        List<Collection> collections = loadCollectionsByUserId(userId);
        List<CollectionData> collectionDatas = collectionDataService.constructCollectionDatas(collections);

        CollectionsData collectionsData = new CollectionsData.CollectionsDataBuilder().setCollections(collectionDatas).build();
        return collectionsData;
    }

    public CollectionsData loadCollectionsDataWithPublishedDocumentCollectionByUser(Long userId) {
        List<Collection> collections = loadCollectionsByUserId(userId);
        List<CollectionData> collectionDatas = collectionDataService.constructCollectionDatas(collections);

        int publishedDocumentCollectionSize = publishedDocumentService.countUsersPublishedDocuments(userId);
        CollectionData publishedDocumentCollection = new CollectionData.CollectionDataBuilder().setCollectionSize(publishedDocumentCollectionSize).build();
        collectionDatas.add(0, publishedDocumentCollection);
        
        CollectionsData collectionsData = new CollectionsData.CollectionsDataBuilder().setCollections(collectionDatas).build();
        return collectionsData;
    }

    public Collection loadCollectionByIdAndUserId(Long id, Long userId) {
        Collection collection = null;
        if (collectionExists(id, userId)) {
            collection = loadById(id);
        }
        return collection;
    }

    public void addPublishedDocumentToCollection(Long collectionId, String publicationHash, Long userId) {
        Collection collection = loadCollectionByIdAndUserId(collectionId, userId);
        List<PublishedDocument> documents = collection.getDocuments();

        PublishedDocument publishedDocument = publishedDocumentService.loadByHash(publicationHash);
        documents.add(publishedDocument);
        save(collection);
    }

    public void deletePublishedDocumentFromCollection(Long collectionId, String publicationHash, Long userId) {
        Collection collection = loadCollectionByIdAndUserId(collectionId, userId);
        List<PublishedDocument> documents = collection.getDocuments();

        List<PublishedDocument> updatedDocuments = documents.stream().filter(d -> !d.getHash().equals(publicationHash)).collect(Collectors.toList());
        collection.setDocuments(updatedDocuments);

        save(collection);
    }

    public void editCollection(EditCollectionRequestData editCollectionRequestData) {
        Long collectionId = editCollectionRequestData.getId();
        Collection collection = loadById(collectionId);

        String updatedName = editCollectionRequestData.getName();
        collection.setName(updatedName);

        save(collection);
    }

    public boolean collectionNameExists(String collectionName, Long userId) {
        return collectionRepository.existsByNameAndUserId(collectionName, userId);
    }

    public boolean collectionExists(Long collectionId, Long userId) {
        return collectionRepository.existsByIdAndUserId(collectionId, userId);
    }

    public void deleteById(Long id) {
        collectionRepository.deleteById(id);
    }

    public void deleteUserCollections(User user) {
        collectionRepository.deleteAllByUser(user);
    }

    public List<UserCollectionData> collectUserCollections(User user) {
        List<UserCollectionData> userCollections = new ArrayList<>();

        List<Collection> collections = loadCollectionsByUserId(user.getId());
        for (Collection collection : collections) {
            UserCollectionData collectionData = constructUserCollectionData(collection);
            userCollections.add(collectionData);
        }
        return userCollections;
    }

    private void addPublishedDocumentIfRequested(CollectionRequestData collectionRequestData, Collection collection) {
        String publication = collectionRequestData.getPublication();
        if (null != publication) {
            PublishedDocument document = publishedDocumentService.loadByHash(publication);
            List<PublishedDocument> collectionDocuments = Arrays.asList(document);
            collection.setDocuments(collectionDocuments);
        }
    }

    private Collection loadById(Long id) {
        Optional<Collection> optionalCollection = collectionRepository.findById(id);
        Collection collection = optionalCollection.isPresent() ? optionalCollection.get() : null;
        return collection;
    }

    private List<Collection> loadCollectionsByUserId(Long userId) {
        List<Collection> collections = collectionRepository.findAllByUserId(userId);
        return collections;
    }

    private Collection save(Collection collection) {
        Collection savedCollection = collectionRepository.save(collection);
        return savedCollection;
    }

    private UserCollectionData constructUserCollectionData(Collection collection) {
        List<PublishedDocument> documents = collection.getDocuments();
        List<String> documentUrls = documents.stream().map(d -> d.getPdfUrl()).collect(Collectors.toList());
        UserCollectionData collectionData = new UserCollectionData.UserCollectionDataBuilder().setName(collection.getName()).setPublishedDocuments(documentUrls).build();
        return collectionData;
    }

}
