
/* generalCacheManager - используется для кеширования объектов не занимающих много памяти.
 * mediaFileCacheManager - для кеширования тежелых объектов, например файлов изображений
 *
 * Параметры MAX_SIZE и LIVE_TIME будут зависеть от кол-ва оперативки на сервере.
 * Их опитимальные значения для каждого из менеджеров нужно будет подобрать тестовым путем.
 *
 * Для наиболее эффективного кеширования, перед его реализацией надо поставить счетчики
 * на действия получения, удаления и изменения данных. Кешировать только те данные,
 * действия получения для которых, производятся на порядок чаще, чем сумма действий удаления и изменения.
 * Иначе обращения к кешу будут происходить реже или ненамного чаще, чем его обновление и очистка.
 */

package ru.java.mentor.oldranger.club.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
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


    @Bean("generalCacheManager")
    @Primary
    public CacheManager generalCacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager(
                "user",
                "article",
                "articleComment",
                "tagNode",
                "topic",
                "photo",
                "photoComment"
        );
        cacheManager.setCaffeine(generalCacheBuilder());
        return cacheManager;
    }

    @Bean("mediaFileCacheManager")
    public CacheManager mediaFileCacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager("photoFile");
        cacheManager.setCaffeine(mediaFileCacheBuilder());
        return cacheManager;
    }

    Caffeine<Object, Object> generalCacheBuilder() {
        return Caffeine.newBuilder()
                .initialCapacity(INIT_CAPACITY)
                .maximumSize(MAX_SIZE)
                .expireAfterAccess(LIVE_TIME, TimeUnit.MINUTES)
                .recordStats();
    }

    Caffeine<Object, Object> mediaFileCacheBuilder() {
        return Caffeine.newBuilder()
                .initialCapacity(INIT_CAPACITY)
                .maximumSize(MAX_SIZE)
                .expireAfterAccess(LIVE_TIME, TimeUnit.MINUTES)
                .recordStats()
                .softValues();
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