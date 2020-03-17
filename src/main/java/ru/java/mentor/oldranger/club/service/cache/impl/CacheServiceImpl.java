package ru.java.mentor.oldranger.club.service.cache.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;
import ru.java.mentor.oldranger.club.service.cache.CacheService;

import java.util.Objects;

@Slf4j
@Service
public class CacheServiceImpl implements CacheService {

    private final CacheManager generalCacheManager;
    private final CacheManager mediaFileCacheManager;

    public CacheServiceImpl(@Qualifier("generalCacheManager") CacheManager generalCacheManager, @Qualifier("mediaFileCacheManager") CacheManager mediaFileCacheManager) {
        this.generalCacheManager = generalCacheManager;
        this.mediaFileCacheManager = mediaFileCacheManager;
    }

    @Override
    public void clearAllCaches() {
        generalCacheManager.getCacheNames().forEach(
                e -> Objects.requireNonNull(generalCacheManager.getCache(e)).clear());
        mediaFileCacheManager.getCacheNames().forEach(
                e -> Objects.requireNonNull(mediaFileCacheManager.getCache(e)).clear());
        log.debug("Cache clear");
    }

}