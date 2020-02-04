package ru.java.mentor.oldranger.club.service.cach.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;
import ru.java.mentor.oldranger.club.service.cach.ThisCacheService;
@Slf4j
@Service
public class ThisCacheServiceImpl implements ThisCacheService {
    @Autowired
    CacheManager cacheManager;

    @Override
    public void clearAllCaches() {

        for (String name :cacheManager.getCacheNames()) {
            cacheManager.getCache(name).clear();
        }
        log.debug("Cache clear");
    }
    @Override
    public void clearCacheAllUsers(){
        cacheManager.getCache("allUsers").clear();
        log.debug("cache allUser clear");
    }


    @Override
    public void clearCacheAllArticle() {
        cacheManager.getCache("allArticle").clear();
        log.debug("cache allArticle clear");
    }

    @Override
    public void clearCacheAllMedia() {
        cacheManager.getCache("allMedia").clear();
        log.debug("cache allMedia clear");
    }

    @Override
    public void clearCacheAllTags() {
        cacheManager.getCache("allTags").clear();
        log.debug("cache allTags clear");
    }

    @Override
    public void clearCacheAllNodeTag() {
        cacheManager.getCache("allTagNode").clear();
        log.debug("cache allTagNode clear");

    }

    @Override
    public void clearCacheAllTopic() {
        cacheManager.getCache("allTopic").clear();
        log.debug("cache allTopic clear");
    }
}
