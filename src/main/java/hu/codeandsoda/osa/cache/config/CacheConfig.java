package hu.codeandsoda.osa.cache.config;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.common.cache.CacheBuilder;

@Configuration
@EnableCaching
public class CacheConfig {

    public static final String DOCUMENT_PUBLISH_IN_PROGRESS_CACHE_NAME = "publishInProgressCache";

    @Bean
    public CacheManager cacheManager() {
        ConcurrentMapCacheManager cacheManager = new ConcurrentMapCacheManager() {
            @Override
            protected Cache createConcurrentMapCache(final String name) {
                return new ConcurrentMapCache(name, CacheBuilder.newBuilder().expireAfterWrite(3, TimeUnit.MINUTES).build().asMap(), false);
            }
        };

        cacheManager.setCacheNames(Arrays.asList(DOCUMENT_PUBLISH_IN_PROGRESS_CACHE_NAME));
        return cacheManager;
       }

   }


