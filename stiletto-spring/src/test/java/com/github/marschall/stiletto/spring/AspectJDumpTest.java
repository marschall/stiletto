package com.github.marschall.stiletto.spring;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.support.NoOpCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import com.github.marschall.stiletto.spring.configuration.SpringCacheableConfiguration;

@SpringJUnitConfig(SpringCacheableConfiguration.class)
//@ContextConfiguration(classes = AspectJCacheableService.class)
public class AspectJDumpTest {

  @Autowired
  private SimpleCacheableInterface service;

  @Test
  public void cacheable() {
    assertEquals("ok", this.service.cacheable("ok"));
    System.out.println(this.service.getClass().getName());
  }

  @Configuration
  @EnableCaching
  public static class AspectJCacheableConfiguration {

    @Bean
    public CacheManager cacheManager() {
      return new NoOpCacheManager();
    }

    @Bean
    public AspectJCacheableService simpleCacheableService() {
      return new AspectJCacheableService();
    }

  }

  @CacheConfig(cacheNames = "default")
  public static class AspectJCacheableService {

    @Cacheable
    public String cacheable(String s) {
      return s;
    }

    @CachePut
    public void cachePut(Long l) {
      // nothing
    }

    @CacheEvict
    public void cacheEvict(Integer i) {
      // nothing
    }

  }

}
