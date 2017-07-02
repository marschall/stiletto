package com.github.marschall.stiletto.processor.el;

public final class JoinPoint {

  private final String methodName;
  private final String methodSignature;

  public JoinPoint(String methodName, String methodSignature) {
    this.methodName = methodName;
    this.methodSignature = methodSignature;
  }

  public String getMethodName() {
    return this.methodName;
  }

  public String getMethodSignature() {
    return this.methodSignature;
  }

}
