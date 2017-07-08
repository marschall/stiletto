package com.github.marschall.stiletto.tests.injection;

import com.github.marschall.stiletto.api.advice.AfterReturning;
import com.github.marschall.stiletto.api.injection.ReturnValue;

public class CaptureReturnValueAspect {

  private Object returnValue;

  @AfterReturning
  public void captureReturnValue(@ReturnValue Object returnValue) {
    this.returnValue = returnValue;
  }

  public Object getReturnValue() {
    return this.returnValue;
  }

}
