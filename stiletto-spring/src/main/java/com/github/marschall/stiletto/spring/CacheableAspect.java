package com.github.marschall.stiletto.spring;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;

import com.github.marschall.stiletto.api.advice.AfterReturning;
import com.github.marschall.stiletto.api.advice.Around;
import com.github.marschall.stiletto.api.advice.Before;
import com.github.marschall.stiletto.api.injection.DeclaredAnnotation;
import com.github.marschall.stiletto.api.injection.MethodCall;
import com.github.marschall.stiletto.api.injection.ReturnValue;
import com.github.marschall.stiletto.api.invocation.ActualMethodCallWithoutResult;

/**
 * Reimplementation of {@link org.springframework.cache.interceptor.CacheInterceptor}.
 */
public class CacheableAspect {

  @Cacheable
  @Around
  public void invoke(@DeclaredAnnotation Cacheable cacheable, @MethodCall ActualMethodCallWithoutResult call) {

  }

  @CachePut
  @AfterReturning
  public void cachePut(@DeclaredAnnotation CachePut cachePut, @ReturnValue Object value) {

  }

  @CacheEvict
  @AfterReturning
  public void cacheEvictAfter(@DeclaredAnnotation CacheEvict cacheEvict) {

  }

  @CacheEvict(beforeInvocation = true)
  @Before
  public void cacheEvictTrue(@DeclaredAnnotation CacheEvict cacheEvict) {

  }

}
