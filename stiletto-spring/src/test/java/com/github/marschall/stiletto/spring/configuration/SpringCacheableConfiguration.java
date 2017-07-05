package com.github.marschall.stiletto.spring.configuration;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.support.NoOpCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.marschall.stiletto.spring.SimpleCacheableInterface;
import com.github.marschall.stiletto.spring.SimpleCacheableService;

@Configuration
@EnableCaching
public class SpringCacheableConfiguration {

  @Bean
  public CacheManager cacheManager() {
    return new NoOpCacheManager();
  }

  @Bean
  public SimpleCacheableInterface simpleCacheableService() {
    return new SimpleCacheableService();
  }

}
