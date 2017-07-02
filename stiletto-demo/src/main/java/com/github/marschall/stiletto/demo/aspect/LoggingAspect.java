package com.github.marschall.stiletto.demo.aspect;

import com.github.marschall.stiletto.api.advice.AfterReturning;
import com.github.marschall.stiletto.api.advice.Before;
import com.github.marschall.stiletto.api.injection.Evaluate;

public class LoggingAspect {

  @Before
  public void logEntering(@Evaluate("${joinpoint.methodName}") String methodName) {
    System.out.println("entering " + methodName);
  }

  @AfterReturning
  public void logExiting(@Evaluate("${joinpoint.methodName}") String methodName) {
    System.out.println("exiting " + methodName);
  }

}
