package com.github.marschall.stiletto.tests.advice;

import com.github.marschall.stiletto.api.advice.Around;
import com.github.marschall.stiletto.api.injection.MethodCall;
import com.github.marschall.stiletto.api.invocation.ActualMethodCall;

public class AroundWithoutReturnValueOrArgumentsAspect {

  private boolean beforePassed;
  private boolean afterPassed;

  public AroundWithoutReturnValueOrArgumentsAspect() {
    this.beforePassed = false;
    this.afterPassed = false;
  }

  @Around
  public <T> T round(@MethodCall ActualMethodCall<T> call) {
    this.beforePassed = true;
    T returnValue = call.invoke();
    this.afterPassed = true;
    return returnValue;
  }

  public boolean isBeforePassed() {
    return this.beforePassed;
  }

  public boolean isAfterPassed() {
    return this.afterPassed;
  }

}
