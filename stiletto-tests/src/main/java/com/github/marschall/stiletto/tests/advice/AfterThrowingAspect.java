package com.github.marschall.stiletto.tests.advice;

import com.github.marschall.stiletto.api.advice.AfterReturning;
import com.github.marschall.stiletto.api.advice.AfterThrowing;

public class AfterThrowingAspect {

  private int invocationCount;

  public AfterThrowingAspect() {
    this.invocationCount = 0;
  }

  @AfterThrowing
  public void afterThrowing() {
    this.invocationCount += 1;
  }

  public int getInvocationCount() {
    return invocationCount;
  }

}
