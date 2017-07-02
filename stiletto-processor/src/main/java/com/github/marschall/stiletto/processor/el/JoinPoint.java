package com.github.marschall.stiletto.processor.el;

public final class JoinPoint {

  private final String methodName;

  public JoinPoint(String methodName) {
    this.methodName = methodName;
  }

  public String getMethodName() {
    return this.methodName;
  }

}
