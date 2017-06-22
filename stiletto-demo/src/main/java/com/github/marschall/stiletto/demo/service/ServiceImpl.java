package com.github.marschall.stiletto.demo.service;

import com.github.marschall.stiletto.api.generation.GenerateAspect;
import com.github.marschall.stiletto.api.generation.GenerateAspects;
import com.github.marschall.stiletto.demo.aspect.TransactionAspect1;

@GenerateAspects(@GenerateAspect(TransactionAspect1.class))
public class ServiceImpl implements IService {

  // also default methods

  @Override
  public String serviceMethod(String s) {
    return "hello " + s;
  }

}
