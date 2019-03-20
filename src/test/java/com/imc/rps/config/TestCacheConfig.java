package com.imc.rps.config;

import com.imc.rps.common.utils.ClassUtils;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan({"com.imc.rps.game.service", "com.imc.rps.game.model"})
@EnableAutoConfiguration
@EnableCaching
public class TestCacheConfig {
    @Bean
    CacheManager cacheManager() {
        return new ConcurrentMapCacheManager(ClassUtils.GAMES_COLLECTION_NAME);
    }
}
