package com.github.marschall.stiletto.tests.hierarchy;

abstract class IntermediateClass implements IntermediateInterface {

  @Override
  public String simpleMethod() {
    return "ok";
  }

  @Override
  public String toString() {
    return '[' + this.getClass().getSimpleName() + ']';
  }

}
