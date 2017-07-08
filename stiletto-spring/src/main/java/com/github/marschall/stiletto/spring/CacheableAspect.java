package com.github.marschall.stiletto.spring;

import java.lang.reflect.Method;
import java.util.Optional;

import org.junit.runners.Parameterized.Parameters;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.cache.interceptor.CacheResolver;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.transaction.annotation.Transactional;

import com.github.marschall.stiletto.api.advice.AfterReturning;
import com.github.marschall.stiletto.api.advice.Around;
import com.github.marschall.stiletto.api.advice.Before;
import com.github.marschall.stiletto.api.advice.WithAnnotationMatching;
import com.github.marschall.stiletto.api.injection.Arguments;
import com.github.marschall.stiletto.api.injection.DeclaredAnnotation;
import com.github.marschall.stiletto.api.injection.Joinpoint;
import com.github.marschall.stiletto.api.injection.MethodCall;
import com.github.marschall.stiletto.api.injection.ReturnValue;
import com.github.marschall.stiletto.api.injection.TargetObject;
import com.github.marschall.stiletto.api.invocation.ActualMethodCallWithoutResult;

/**
 * Reimplementation of {@link org.springframework.cache.interceptor.CacheInterceptor}.
 */
public class CacheableAspect {

  private static final String[] EMPTY = new String[0];

  private final KeyGenerator keyGenerator;
  private final CacheManager cacheManager;
  private final CacheResolver cacheResolver;
  private final CacheErrorHandler errorHandler;
  private final String[] cacheNames;

  public CacheableAspect(KeyGenerator keyGenerator, CacheManager cacheManager, CacheResolver cacheResolver, CacheErrorHandler errorHandler, String... cacheNames) {
    this.keyGenerator = keyGenerator;
    this.cacheManager = cacheManager;
    this.cacheResolver = cacheResolver;
    this.errorHandler = errorHandler;
    this.cacheNames = cacheNames;
  }

  @Around
  @Cacheable
  @WithAnnotationMatching(Cacheable.class)
  public void invoke(@DeclaredAnnotation Cacheable cacheable, @MethodCall ActualMethodCallWithoutResult call) {

  }

  @AfterReturning
  @CachePut
  @WithAnnotationMatching(CachePut.class)
  public void cachePut(@DeclaredAnnotation CachePut cachePut, @ReturnValue Object value) {

  }

  // org.springframework.cache.interceptor.CacheAspectSupport.performCacheEvict(CacheOperationContext, CacheEvictOperation, Object)
  @AfterReturning
  @CacheEvict(beforeInvocation = false, allEntries = true)
  @WithAnnotationMatching(CacheEvict.class)
  public void cacheEvictAllAfter(
          @DeclaredAnnotation CacheEvict cacheEvict,
          @DeclaredAnnotation Optional<CacheConfig> cacheConfig) {
    // TODO sync
    evictAllEntries(cacheEvict, cacheConfig);
  }

  @AfterReturning
  @CacheEvict(beforeInvocation = false, allEntries = false)
  @WithAnnotationMatching(CacheEvict.class)
  public void cacheEvictAfter(
          @DeclaredAnnotation CacheEvict cacheEvict,
          @DeclaredAnnotation Optional<CacheConfig> cacheConfig,
          @TargetObject Object targetObject,
          @Joinpoint Method method,
          @Arguments Object[] arguments) {
    // TODO sync
    evictOneEntry(cacheEvict, cacheConfig, targetObject, method, arguments);
  }

  @Before
  @CacheEvict(beforeInvocation = true, allEntries = true)
  @WithAnnotationMatching(CacheEvict.class)
  public void cacheEvictAllBefore(
          @DeclaredAnnotation CacheEvict cacheEvict,
          @DeclaredAnnotation Optional<CacheConfig> cacheConfig) {
    // TODO sync
    evictAllEntries(cacheEvict, cacheConfig);
  }

  @Before
  @CacheEvict(beforeInvocation = true, allEntries = false)
  @WithAnnotationMatching(CacheEvict.class)
  public void cacheEvictBefore(
          @DeclaredAnnotation CacheEvict cacheEvict,
          @DeclaredAnnotation Optional<CacheConfig> cacheConfig,
          @TargetObject Object targetObject,
          @Joinpoint Method method,
          @Arguments Object[] arguments) {
    // TODO sync
    evictOneEntry(cacheEvict, cacheConfig, targetObject, method, arguments);
  }

  private void evictOneEntry(CacheEvict cacheEvict,
          Optional<CacheConfig> cacheConfig, Object targetObject, Method method,
          Object[] arguments) {
    for (String cacheName : this.getCacheNames(cacheEvict, cacheConfig)) {
      Cache cache = this.cacheManager.getCache(cacheName);
      Object key = this.keyGenerator.generate(targetObject, method, arguments);
      try {
        cache.evict(key);
      } catch (RuntimeException e) {
        this.errorHandler.handleCacheEvictError(e, cache, key);
      }
    }
  }

  private void evictAllEntries(CacheEvict cacheEvict, Optional<CacheConfig> cacheConfig) {
    for (String cacheName : this.getCacheNames(cacheEvict, cacheConfig)) {
      Cache cache = this.cacheManager.getCache(cacheName);
      try {
        cache.clear();
      } catch (RuntimeException e) {
        this.errorHandler.handleCacheClearError(e, cache);
      }
    }
  }

  private String[] getCacheNames(CacheEvict cacheEvict, Optional<CacheConfig> cacheConfig) {
    String[] names = this.computeCacheNames(cacheEvict, cacheConfig);
    if (names.length == 0) {
      throw new IllegalArgumentException("no cache names specified");
    }
    return names;
  }

  private String[] computeCacheNames(CacheEvict cacheEvict, Optional<CacheConfig> cacheConfig) {
    if (this.cacheNames.length != 0) {
      return this.cacheNames;
    }
    if (cacheEvict.cacheNames().length != 0) {
      return cacheEvict.cacheNames();
    }
    if (cacheEvict.value().length != 0) {
      return cacheEvict.value();
    }
    if (cacheConfig.isPresent()) {
      return cacheConfig.get().cacheNames();
    }
    return EMPTY;
  }

}
