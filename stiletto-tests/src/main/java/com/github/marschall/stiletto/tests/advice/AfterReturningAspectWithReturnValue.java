package com.github.marschall.stiletto.tests.advice;

import com.github.marschall.stiletto.api.advice.AfterReturning;

public class AfterReturningAspectWithReturnValue {

  @AfterReturning
  public Object afterFinally() {
    return "called";
  }

}
