package com.github.marschall.stiletto.demo.service;

import com.github.marschall.stiletto.demo.aspect.LoggingAspect;

public class IService__ implements IService {

  private final IService__ delegate;
  private final LoggingAspect aspect;

  public IService__(IService__ delegate, LoggingAspect aspect) {
    this.delegate = delegate;
    this.aspect = aspect;
  }

  @Override
  public String serviceMethod(String s) {
    this.aspect.logEntering("serviceMethod");
    String returnValue = this.delegate.serviceMethod(s);
    this.aspect.logExiting("serviceMethod");
    return returnValue;
  }

  // TODO delegate all other methods
  // TODO delegate default
  // TODO toString?

}
