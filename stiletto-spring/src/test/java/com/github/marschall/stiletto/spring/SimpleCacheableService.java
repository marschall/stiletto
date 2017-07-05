package com.github.marschall.stiletto.spring;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;

//@AdviseBy(CacheableAspect.class)
public class SimpleCacheableService {

  @Cacheable
  public String cacheable(String s) {
    return s + ":" + s;
  }

  @CachePut
  public String cachePut(String s) {
    return s + ":" + s;
  }

  @CacheEvict
  public String cacheEvict(String s) {
    return s + ":" + s;
  }

}
