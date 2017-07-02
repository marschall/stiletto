package com.github.marschall.stiletto.demo.aspect;

import com.github.marschall.stiletto.api.advice.AfterReturning;
import com.github.marschall.stiletto.api.advice.Before;
import com.github.marschall.stiletto.api.injection.Evaluate;

public class LoggingAspect {

  @Before
  public void logEntering(@Evaluate("entering ${targetClass.fullyQualifiedName}.${joinpoint.methodSignature}") String logMessage) {
    System.out.println(logMessage);
  }

  @AfterReturning
  public void logExiting(@Evaluate("exiting ${targetClass.fullyQualifiedName}.${joinpoint.methodSignature}") String logMessage) {
    System.out.println(logMessage);
  }

}
