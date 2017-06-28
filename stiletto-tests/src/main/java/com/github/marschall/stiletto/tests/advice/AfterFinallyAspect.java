package com.github.marschall.stiletto.tests.advice;

import com.github.marschall.stiletto.api.advice.AfterFinally;

public class AfterFinallyAspect {

  private int invocationCount;

  public AfterFinallyAspect() {
    this.invocationCount = 0;
  }

  @AfterFinally
  public void afterFinally() {
    this.invocationCount += 1;
  }

  public int getInvocationCount() {
    return invocationCount;
  }

}
