package com.github.marschall.stiletto.tests.advice;

import com.github.marschall.stiletto.api.advice.AfterReturning;

public class AfterReturningAspect {

  private int invocationCount;

  public AfterReturningAspect() {
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
