package com.github.marschall.stiletto.tests.injection;

import com.github.marschall.stiletto.api.advice.AfterReturning;
import com.github.marschall.stiletto.api.injection.ExecutionTimeNanos;

public class CaptureExecutionTimeNanosAspect {

  private long nanos;

  @AfterReturning
  public void captureReturnValue(@ExecutionTimeNanos long nanos) {
    this.nanos = nanos;
  }

  public long getNanos() {
    return this.nanos;
  }

}
