package com.github.marschall.stiletto.tests.advice;

import com.github.marschall.stiletto.api.advice.AfterThrowing;

public class AfterThrowingAspectWithReturnValue {

  @AfterThrowing
  public Object afterFinally() {
    return "called";
  }

}
