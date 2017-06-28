package com.github.marschall.stiletto.tests.advice;

import com.github.marschall.stiletto.api.advice.Around;
import com.github.marschall.stiletto.api.invocation.ActualMethodCallWithoutResult;

public class AroundWithoutReturnValueOrArgumentsAspect {

  private boolean beforePassed;
  private boolean afterPassed;

  public AroundWithoutReturnValueOrArgumentsAspect() {
    this.beforePassed = false;
    this.afterPassed = false;
  }

  @Around
  public void round(ActualMethodCallWithoutResult call) {
    this.beforePassed = true;
    call.invoke();
    this.afterPassed = true;
  }

  public boolean isBeforePassed() {
    return this.beforePassed;
  }

  public boolean isAfterPassed() {
    return this.afterPassed;
  }

}
