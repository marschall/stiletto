package com.github.marschall.stiletto.spring;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import com.github.marschall.stiletto.spring.SpringCacheableTest.SpringCacheableConfiguration;
import com.github.marschall.stiletto.spring.configuration.CacheableConfiguration;

@SpringJUnitConfig({CacheableConfiguration.class, SpringCacheableConfiguration.class})
public class SpringCacheableTest {

  @Autowired
  private SimpleCacheableInterface service;

  @Test
  public void cacheable() {
    assertEquals("ok", this.service.cacheable("ok"));
  }

  @Test
  public void cachePut() {
    this.service.cachePut(23L);
  }

  @Test
  public void cacheEvict() {
    this.service.cacheEvict(42);
  }

  @Configuration
  @EnableCaching
  static class SpringCacheableConfiguration {

    @Bean
    public SimpleCacheableInterface simpleCacheableService() {
      return new SimpleCacheableService();
    }

  }


}
