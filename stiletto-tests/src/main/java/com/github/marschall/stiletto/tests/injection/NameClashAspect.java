package com.github.marschall.stiletto.tests.injection;

import com.github.marschall.stiletto.api.advice.AfterReturning;
import com.github.marschall.stiletto.api.injection.ExecutionTimeMillis;
import com.github.marschall.stiletto.api.injection.ExecutionTimeNanos;
import com.github.marschall.stiletto.api.injection.ReturnValue;

public class NameClashAspect {

  @AfterReturning
  public void afterReturning(@ReturnValue Object returnValue,
          @ExecutionTimeMillis long exectionTimeMillis,
          @ExecutionTimeNanos long exectionTimeNanos) {

  }

}
