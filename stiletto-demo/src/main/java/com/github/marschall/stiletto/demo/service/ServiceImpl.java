package com.github.marschall.stiletto.demo.service;

import com.github.marschall.stiletto.api.generation.AdviseBy;
import com.github.marschall.stiletto.api.generation.AdviseByAll;
import com.github.marschall.stiletto.demo.aspect.TransactionAspect1;

@AdviseByAll(@AdviseBy(TransactionAspect1.class))
public class ServiceImpl implements IService {

  // also default methods

  @Override
  public String serviceMethod(String s) {
    return "hello " + s;
  }

}
