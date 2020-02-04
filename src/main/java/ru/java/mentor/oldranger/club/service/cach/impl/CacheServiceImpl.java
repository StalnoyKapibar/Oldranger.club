package ru.java.mentor.oldranger.club.service.cach.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;
import ru.java.mentor.oldranger.club.service.cach.CacheService;

@Slf4j
@Service
public class CacheServiceImpl implements CacheService {
    @Autowired
    CacheManager cacheManager;

    @Override
    public void clearAllCaches() {

        for (String name : cacheManager.getCacheNames()) {
            cacheManager.getCache(name).clear();
        }
        log.debug("Cache clear");
    }

}
