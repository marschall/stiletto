package com.github.marschall.stiletto.demo.service;

public class ServiceImpl implements IService {

  // also default methods

  @Override
  public String serviceMethod(String s) {
    return "hello " + s;
  }

}
