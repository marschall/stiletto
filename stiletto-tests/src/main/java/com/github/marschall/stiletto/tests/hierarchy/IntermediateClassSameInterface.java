package com.github.marschall.stiletto.tests.hierarchy;

abstract class IntermediateClassSameInterface implements IntermediateInterface {

  @Override
  public String simpleMethod() {
    return "ok";
  }

  @Override
  public String toString() {
    return '[' + this.getClass().getSimpleName() + ']';
  }

}
