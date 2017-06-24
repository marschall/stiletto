package com.github.marschall.stiletto.demo.service;

import com.github.marschall.stiletto.api.generation.ApplyAspect;
import com.github.marschall.stiletto.api.generation.ApplyAspects;
import com.github.marschall.stiletto.demo.aspect.TransactionAspect1;

@ApplyAspects(@ApplyAspect(TransactionAspect1.class))
public class ServiceImpl implements IService {

  // also default methods

  @Override
  public String serviceMethod(String s) {
    return "hello " + s;
  }

}
