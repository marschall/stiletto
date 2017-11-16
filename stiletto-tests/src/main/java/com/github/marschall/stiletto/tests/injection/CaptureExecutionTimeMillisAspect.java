package com.github.marschall.stiletto.tests.injection;

import com.github.marschall.stiletto.api.advice.AfterReturning;
import com.github.marschall.stiletto.api.injection.ExecutionTimeMillis;

public class CaptureExecutionTimeMillisAspect {

  private long millis;

  @AfterReturning
  public void captureReturnValue(@ExecutionTimeMillis long millis) {
    this.millis = millis;
  }

  public long getMillis() {
    return millis;
  }

}
