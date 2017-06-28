package com.github.marschall.stiletto.tests.advice;

import com.github.marschall.stiletto.api.advice.AfterReturning;

public class AfterThrowingAspect {

  private int invocationCount;

  public AfterThrowingAspect() {
    this.invocationCount = 0;
  }

  @AfterReturning
  public void afterFinally() {
    this.invocationCount += 1;
  }

  public int getInvocationCount() {
    return invocationCount;
  }

}
