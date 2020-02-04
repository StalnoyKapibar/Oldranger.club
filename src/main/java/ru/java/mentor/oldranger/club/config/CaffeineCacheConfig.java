package ru.java.mentor.oldranger.club.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

@Configuration
@EnableCaching
public class CaffeineCacheConfig {
    @Value("${INIT_CAPACITY}")
    private int INIT_CAPACITY;
    @Value("${MAX_SIZE}")
    private int MAX_SIZE;
    @Value("${LIVE_TIME}")
    private long LIVE_TIME;

    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager(
                "users",
                "allUsers",
                "article",
                "allArticle",
                "tagNode",
                "allTagNode",
                "topic",
                "allTopic"
                );
        cacheManager.setCaffeine(caffeineCacheBuilder());
        return cacheManager;
    }

    Caffeine<Object, Object> caffeineCacheBuilder() {
        return Caffeine.newBuilder()
                .initialCapacity(INIT_CAPACITY)
                .maximumSize(MAX_SIZE)
                .expireAfterAccess(LIVE_TIME, TimeUnit.MINUTES)
                .weakKeys()
                .recordStats();
    }
    public class CustomKeyGenerator implements KeyGenerator {

        public Object generate(Object target, Method method, Object... params) {
            return target.getClass().getSimpleName() + "_"
                    + method.getName() + "_"
                    + StringUtils.arrayToDelimitedString(params, "_");
        }
    }
    @Bean("customKeyGenerator")
    public KeyGenerator keyGenerator() {
        return new CustomKeyGenerator();
    }
}