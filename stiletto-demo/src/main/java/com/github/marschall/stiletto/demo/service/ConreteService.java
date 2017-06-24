package com.github.marschall.stiletto.demo.service;

import com.github.marschall.stiletto.api.generation.AdviseBy;
import com.github.marschall.stiletto.demo.aspect.LoggingAspect;

@AdviseBy(LoggingAspect.class)
public class ConreteService {

  public String serviceMethod(String s) {
    return "hello " + s;
  }

}
