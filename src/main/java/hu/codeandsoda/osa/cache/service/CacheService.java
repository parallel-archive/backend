package hu.codeandsoda.osa.cache.service;

import java.text.MessageFormat;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import hu.codeandsoda.osa.account.user.domain.User;
import hu.codeandsoda.osa.cache.config.CacheConfig;
import hu.codeandsoda.osa.documentpublish.data.PublishedDocumentStatus;
import hu.codeandsoda.osa.documentpublish.domain.PublishedDocument;
import hu.codeandsoda.osa.documentpublish.repository.PublishedDocumentRepository;

@Service
public class CacheService {

    private static final String UPDATE_PUBLISH_IN_PROGRESS_CACHE_ACTION = "updatePublishInProgressCache";

    private static Logger logger = LoggerFactory.getLogger(CacheService.class);

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private PublishedDocumentRepository publishedDocumentRepository;

    public boolean userHasPublishInProgress(Long userId) {
        Cache cache = cacheManager.getCache(CacheConfig.DOCUMENT_PUBLISH_IN_PROGRESS_CACHE_NAME);

        boolean userHasPublishInProgress = publishInProgressCacheContainsUserId(cache, userId);
        if (!userHasPublishInProgress) {
            updatePublishInProgressCache(cache);
            userHasPublishInProgress = publishInProgressCacheContainsUserId(cache, userId);
        }

        return userHasPublishInProgress;
    }

    private Boolean publishInProgressCacheContainsUserId(Cache cache, Long userId) {
        Boolean userHasPublishInProgress = null != cache.get(userId);
        return userHasPublishInProgress;
    }

    private void updatePublishInProgressCache(Cache cache) {
        String startLogMessage = MessageFormat.format("action={0}, status=start", UPDATE_PUBLISH_IN_PROGRESS_CACHE_ACTION);
        logger.info(startLogMessage);

        List<PublishedDocument> documents = publishedDocumentRepository.findByStatus(PublishedDocumentStatus.IN_PROGRESS);
        Set<User> cachedUsers = documents.stream().map(d -> d.getUser()).collect(Collectors.toSet());
        for (User cachedUser : cachedUsers) {
            cache.put(cachedUser.getId(), Boolean.TRUE);
        }

        String endLogMessage = MessageFormat.format("action={0}, status=success", UPDATE_PUBLISH_IN_PROGRESS_CACHE_ACTION);
        logger.info(endLogMessage);
    }

}
