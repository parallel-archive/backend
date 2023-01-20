package hu.codeandsoda.osa.document.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import hu.codeandsoda.osa.document.domain.DocumentTag;
import hu.codeandsoda.osa.document.repository.DocumentTagRepository;
import hu.codeandsoda.osa.documentpublish.data.PublicArchivePageFilteringRequest;
import hu.codeandsoda.osa.search.service.DocumentTagIndexService;

@Service
public class DocumentTagService {

    @Autowired
    private DocumentTagRepository documentTagRepository;

    @Autowired
    private DocumentTagIndexService documentTagIndexService;

    public List<DocumentTag> createAndLoadTags(List<String> tagNames) {
        List<DocumentTag> tags = new ArrayList<>();
        for (String tagName : tagNames) {
            boolean tagExists = documentTagRepository.existsByName(tagName);
            DocumentTag tag = tagExists ? loadByName(tagName) : createTag(tagName);
            tags.add(tag);
        }
        return tags;
    }

    public List<String> collectTagNames(List<DocumentTag> documentTags) {
        List<String> tagNames = documentTags.stream().map(t -> t.getName()).collect(Collectors.toList());
        return tagNames;
    }

    public void collectAndValidateActiveTags(PublicArchivePageFilteringRequest publicArchivePageFilteringRequest, List<String> tagNames) {
        List<DocumentTag> tags = loadTagsByName(tagNames);
        publicArchivePageFilteringRequest.setActiveTags(tags);
    }

    private List<DocumentTag> loadTagsByName(List<String> tagNames) {
        List<DocumentTag> tags = new ArrayList<>();
        if (null != tagNames) {
            for (String tagName : tagNames) {
                DocumentTag tag = loadByName(tagName);
                if (null != tag) {
                    tags.add(tag);
                }
            }
        }
        return tags;
    }

    private DocumentTag loadByName(String tagName) {
        return documentTagRepository.findByName(tagName);
    }

    private DocumentTag createTag(String tagName) {
        DocumentTag tag = new DocumentTag();
        tag.setName(tagName);

        DocumentTag savedTag = save(tag);
        return savedTag;
    }

    private DocumentTag save(DocumentTag tag) {
        DocumentTag savedTag = documentTagRepository.save(tag);
        documentTagIndexService.indexDocumentTag(savedTag);
        return savedTag;
    }

}
