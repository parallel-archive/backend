package hu.codeandsoda.osa.documentpublish.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import hu.codeandsoda.osa.cache.service.CacheService;
import hu.codeandsoda.osa.documentpublish.data.DocumentPublishProgressResponse;

@Service
public class DocumentPublishProgressService {

    @Autowired
    private CacheService cacheService;

    public DocumentPublishProgressResponse loadUserDocumentPublishProgressStatus(Long userId) {
        boolean userHasPublishInProgress = cacheService.userHasPublishInProgress(userId);
        
        DocumentPublishProgressResponse response = new DocumentPublishProgressResponse.DocumentPublishProgressResponseBuilder().setPublishInProgress(userHasPublishInProgress)
                .build();
        return response;
    }

}
