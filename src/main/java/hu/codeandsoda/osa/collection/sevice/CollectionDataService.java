package hu.codeandsoda.osa.collection.sevice;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import hu.codeandsoda.osa.collection.data.CollectionData;
import hu.codeandsoda.osa.collection.domain.Collection;
import hu.codeandsoda.osa.documentpublish.data.PublishedDocumentStatus;

@Service
public class CollectionDataService {

    public List<CollectionData> constructCollectionDatas(List<Collection> collections) {
        List<CollectionData> collectionDatas = new ArrayList<>();
        for (Collection collection : collections) {
            CollectionData collectionData = constructCollectionData(collection);
            collectionDatas.add(collectionData);
        }
        return collectionDatas;
    }

    public CollectionData constructCollectionData(Collection collection) {
        int collectionSize = (int) collection.getDocuments().stream().filter(d -> PublishedDocumentStatus.PUBLISHED.equals(d.getStatus())).count();
        CollectionData collectionData = new CollectionData.CollectionDataBuilder().setId(collection.getId()).setName(collection.getName()).setCollectionSize(collectionSize)
                .build();
        return collectionData;
    }

}
