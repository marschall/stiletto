package com.github.marschall.stiletto.tests.generic;

public class GenericAdvisedService<T> implements GenericInterface<T> {

  private T t;

  public GenericAdvisedService(T t) {
    this.t = t;
  }

  @Override
  public T genericMethod() {
    return this.t;
  }

}
