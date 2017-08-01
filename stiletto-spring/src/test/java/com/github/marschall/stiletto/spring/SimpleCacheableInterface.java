package com.github.marschall.stiletto.spring;

public interface SimpleCacheableInterface {

  String cacheable(String s);

  void cachePut(Long l);

  void cacheEvict(Integer i);

}