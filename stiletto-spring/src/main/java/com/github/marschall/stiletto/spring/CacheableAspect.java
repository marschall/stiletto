package com.github.marschall.stiletto.spring;

import org.springframework.cache.annotation.Cacheable;

import com.github.marschall.stiletto.api.advice.Around;
import com.github.marschall.stiletto.api.injection.DeclaredAnnotation;
import com.github.marschall.stiletto.api.injection.MethodCall;
import com.github.marschall.stiletto.api.invocation.ActualMethodCallWithoutResult;

/**
 * Reimplementation of {@link org.springframework.cache.interceptor.CacheInterceptor}.
 */
public class CacheableAspect {

  @Around
  public void invoke(@DeclaredAnnotation Cacheable cacheable, @MethodCall ActualMethodCallWithoutResult call) {

  }

}
