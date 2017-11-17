package com.github.marschall.stiletto.spring.configuration;

import org.springframework.cache.CacheManager;
import org.springframework.cache.support.NoOpCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CacheableConfiguration {

  @Bean
  public CacheManager cacheManager() {
    return new NoOpCacheManager();
  }

}
