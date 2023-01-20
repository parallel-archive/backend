package hu.codeandsoda.osa.document.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import hu.codeandsoda.osa.document.domain.DocumentTag;
import hu.codeandsoda.osa.documentpublish.domain.PublishedDocument;

@Service
public class DocumentTagDataService {

    public Set<String> collectInactiveTagNamesFromDocuments(List<PublishedDocument> filteredPublishedDocuments, List<DocumentTag> activeTags) {
        Set<String> tagNames = new HashSet<>();
        for (PublishedDocument document : filteredPublishedDocuments) {
            List<String> inactiveTagNames = document.getPublishedDocumentMetaData().getTags().stream().filter(t -> !activeTags.contains(t)).map(t -> t.getName())
                    .collect(Collectors.toList());
            tagNames.addAll(inactiveTagNames);
        }
        return tagNames;
    }

    public List<String> collectTagNames(List<DocumentTag> tags) {
        List<String> tagNames = new ArrayList<>();
        for (DocumentTag tag : tags) {
            tagNames.add(tag.getName());
        }
        return tagNames;
    }

}
