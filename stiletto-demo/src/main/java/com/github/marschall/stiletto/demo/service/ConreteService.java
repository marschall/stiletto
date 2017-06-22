package com.github.marschall.stiletto.demo.service;

import com.github.marschall.stiletto.api.generation.GenerateAspect;
import com.github.marschall.stiletto.demo.aspect.LoggingAspect;

@GenerateAspect(LoggingAspect.class)
public class ConreteService {

  public String serviceMethod(String s) {
    return "hello " + s;
  }

}
