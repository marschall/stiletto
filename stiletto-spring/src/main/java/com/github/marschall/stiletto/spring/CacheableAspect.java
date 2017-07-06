package com.github.marschall.stiletto.spring;

import java.util.Optional;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.interceptor.CacheResolver;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.transaction.annotation.Transactional;

import com.github.marschall.stiletto.api.advice.AfterReturning;
import com.github.marschall.stiletto.api.advice.Around;
import com.github.marschall.stiletto.api.advice.Before;
import com.github.marschall.stiletto.api.advice.WithAnnotationMatching;
import com.github.marschall.stiletto.api.injection.DeclaredAnnotation;
import com.github.marschall.stiletto.api.injection.MethodCall;
import com.github.marschall.stiletto.api.injection.ReturnValue;
import com.github.marschall.stiletto.api.invocation.ActualMethodCallWithoutResult;

/**
 * Reimplementation of {@link org.springframework.cache.interceptor.CacheInterceptor}.
 */
public class CacheableAspect {

  private final KeyGenerator keyGenerator;
  private final CacheManager cacheManager;
  private final CacheResolver cacheResolver;
  private final String[] cacheNames;

  public CacheableAspect(KeyGenerator keyGenerator, CacheManager cacheManager, CacheResolver cacheResolver, String... cacheNames) {
    this.keyGenerator = keyGenerator;
    this.cacheManager = cacheManager;
    this.cacheResolver = cacheResolver;
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

  @AfterReturning
  @CacheEvict(beforeInvocation = false)
  @WithAnnotationMatching(CacheEvict.class)
  public void cacheEvictAfter(@DeclaredAnnotation CacheEvict cacheEvict, @DeclaredAnnotation Optional<CacheConfig> cacheConfig) {
    if (cacheEvict.allEntries()) {

    } else {

    }
  }

  @Before
  @CacheEvict(beforeInvocation = true)
  @WithAnnotationMatching(CacheEvict.class)
  public void cacheEvictTrue(@DeclaredAnnotation CacheEvict cacheEvict) {
    if (cacheEvict.allEntries()) {

    } else {

    }
  }

}
