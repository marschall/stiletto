package com.github.marschall.stiletto.spring;

import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;

//@AdviseBy(CacheableAspect.class)
@CacheConfig(cacheNames = "default")
public class SimpleCacheableService implements SimpleCacheableInterface {

  @Override
  @Cacheable
  public String cacheable(String s) {
    return s;
  }

  @Override
  @CachePut
  public void cachePut(Long l) {
    // nothing
  }

  @Override
  @CacheEvict
  public void cacheEvict(Integer i) {
    // nothing
  }

}
