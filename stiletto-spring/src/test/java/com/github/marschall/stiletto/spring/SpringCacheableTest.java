package com.github.marschall.stiletto.spring;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import com.github.marschall.stiletto.spring.configuration.SpringCacheableConfiguration;

@ContextConfiguration(classes = SpringCacheableConfiguration.class)
public class SpringCacheableTest extends AbstractSpringTest {

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

}
