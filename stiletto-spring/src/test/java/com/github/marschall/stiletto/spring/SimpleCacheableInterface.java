package com.github.marschall.stiletto.spring;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;

public interface SimpleCacheableInterface {

  String cacheable(String s);

  void cachePut(Long l);

  void cacheEvict(Integer i);

}