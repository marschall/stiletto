package com.github.marschall.stiletto.tests.advice;

import com.github.marschall.stiletto.api.advice.AfterFinally;

public class AfterFinallyAspectWithReturnValue {

  @AfterFinally
  public Object afterFinally() {
    return "called";
  }

}
