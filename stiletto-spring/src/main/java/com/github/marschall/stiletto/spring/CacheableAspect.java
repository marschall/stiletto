package com.github.marschall.stiletto.spring;

import java.lang.reflect.Method;
import java.util.Optional;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.cache.interceptor.CacheResolver;
import org.springframework.cache.interceptor.KeyGenerator;

import com.github.marschall.stiletto.api.advice.AfterReturning;
import com.github.marschall.stiletto.api.advice.Around;
import com.github.marschall.stiletto.api.advice.Before;
import com.github.marschall.stiletto.api.injection.Arguments;
import com.github.marschall.stiletto.api.injection.DeclaredAnnotation;
import com.github.marschall.stiletto.api.injection.Joinpoint;
import com.github.marschall.stiletto.api.injection.MethodCall;
import com.github.marschall.stiletto.api.injection.ReturnValue;
import com.github.marschall.stiletto.api.injection.TargetObject;
import com.github.marschall.stiletto.api.invocation.ActualMethodCall;
import com.github.marschall.stiletto.api.pointcut.Matching;

/**
 * Reimplementation of {@link org.springframework.cache.interceptor.CacheInterceptor}.
 *
 * <h2>No Supported</h2>
 * The following features of Spring caching are currently not supported:
 * <ul>
 *  <li>specifying {@link CacheResolver}, currently only the cache names are used</li>
 *  <li>conditional caching</li>
 * </ul>
 */
public class CacheableAspect {

  // TODO wrap/unwrap optionals
  // org.springframework.cache.interceptor.CacheAspectSupport.wrapCacheValue(Method, Object)
  // org.springframework.cache.interceptor.CacheAspectSupport.unwrapReturnValue(Object)

  private static final String[] EMPTY = new String[0];

  private final KeyGenerator keyGenerator;
  private final CacheManager cacheManager;
  private final CacheErrorHandler errorHandler;
  private final String[] cacheNames;

  public CacheableAspect(KeyGenerator keyGenerator, CacheManager cacheManager, CacheErrorHandler errorHandler, String... cacheNames) {
    this.keyGenerator = keyGenerator;
    this.cacheManager = cacheManager;
    this.errorHandler = errorHandler;
    this.cacheNames = cacheNames;
  }

  @Around
  @Cacheable
  @Matching(Cacheable.class)
  public <R> R invoke(@DeclaredAnnotation Cacheable cacheable,
          @DeclaredAnnotation Optional<CacheConfig> cacheConfig,
          @TargetObject Object targetObject,
          @Joinpoint Method method,
          @Arguments Object[] arguments,
          @ReturnValue Object value,
          @MethodCall ActualMethodCall<R> call) {
    return call.invoke();
  }

  @AfterReturning
  @CachePut
  @Matching(CachePut.class)
  public void cachePut(@DeclaredAnnotation CachePut cachePut,
          @DeclaredAnnotation Optional<CacheConfig> cacheConfig,
          @TargetObject Object targetObject,
          @Joinpoint Method method,
          @Arguments Object[] arguments,
          @ReturnValue Object value) {
    for (String cacheName : this.getCacheNames(cachePut, cacheConfig)) {
      Cache cache = this.cacheManager.getCache(cacheName);
      Object key = this.keyGenerator.generate(targetObject, method, arguments);
      try {
        cache.put(key, value);
      } catch (RuntimeException e) {
        this.errorHandler.handleCachePutError(e, cache, key, value);
      }
    }

  }

  // org.springframework.cache.interceptor.CacheAspectSupport.performCacheEvict(CacheOperationContext, CacheEvictOperation, Object)
  @AfterReturning
  @CacheEvict(beforeInvocation = false, allEntries = true)
  @Matching(CacheEvict.class)
  public void cacheEvictAllAfter(
          @DeclaredAnnotation CacheEvict cacheEvict,
          @DeclaredAnnotation Optional<CacheConfig> cacheConfig) {
    // TODO sync
    evictAllEntries(cacheEvict, cacheConfig);
  }

  @AfterReturning
  @CacheEvict(beforeInvocation = false, allEntries = false)
  @Matching(CacheEvict.class)
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
  @Matching(CacheEvict.class)
  public void cacheEvictAllBefore(
          @DeclaredAnnotation CacheEvict cacheEvict,
          @DeclaredAnnotation Optional<CacheConfig> cacheConfig) {
    // TODO sync
    evictAllEntries(cacheEvict, cacheConfig);
  }

  @Before
  @CacheEvict(beforeInvocation = true, allEntries = false)
  @Matching(CacheEvict.class)
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

  private String[] getCacheNames(CachePut cachePut, Optional<CacheConfig> cacheConfig) {
    String[] names = this.computeCacheNames(cachePut, cacheConfig);
    if (names.length == 0) {
      throw noCacheNameSpecified();
    }
    return names;
  }

  private String[] computeCacheNames(CachePut cachePut, Optional<CacheConfig> cacheConfig) {
    if (this.cacheNames.length != 0) {
      return this.cacheNames;
    }
    if (cachePut.cacheNames().length != 0) {
      return cachePut.cacheNames();
    }
    if (cachePut.value().length != 0) {
      return cachePut.value();
    }
    if (cacheConfig.isPresent()) {
      return cacheConfig.get().cacheNames();
    }
    return EMPTY;
  }

  private String[] getCacheNames(Cacheable cacheable, Optional<CacheConfig> cacheConfig) {
    String[] names = this.computeCacheNames(cacheable, cacheConfig);
    if (names.length == 0) {
      throw noCacheNameSpecified();
    }
    return names;
  }

  private String[] computeCacheNames(Cacheable cacheable, Optional<CacheConfig> cacheConfig) {
    if (this.cacheNames.length != 0) {
      return this.cacheNames;
    }
    if (cacheable.cacheNames().length != 0) {
      return cacheable.cacheNames();
    }
    if (cacheable.value().length != 0) {
      return cacheable.value();
    }
    if (cacheConfig.isPresent()) {
      return cacheConfig.get().cacheNames();
    }
    return EMPTY;
  }

  private String[] getCacheNames(CacheEvict cacheEvict, Optional<CacheConfig> cacheConfig) {
    String[] names = this.computeCacheNames(cacheEvict, cacheConfig);
    if (names.length == 0) {
      throw noCacheNameSpecified();
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

  private IllegalStateException noCacheNameSpecified() {
    // org.springframework.cache.interceptor.CacheAspectSupport.getCaches(CacheOperationInvocationContext<CacheOperation>, CacheResolver)
    return new IllegalStateException("No cache could be resolved. At least one cache should be provided per cache operation.");
  }

}
