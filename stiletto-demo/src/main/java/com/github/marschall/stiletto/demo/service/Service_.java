package com.github.marschall.stiletto.demo.service;

import com.github.marschall.stiletto.demo.aspect.LoggingAspect;

public class Service_ extends Service {

  private final Service delegate;
  private final LoggingAspect aspect;

  public Service_(Service delegate, LoggingAspect aspect) {
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

}
