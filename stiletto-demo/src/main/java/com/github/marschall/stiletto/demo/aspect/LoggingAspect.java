package com.github.marschall.stiletto.demo.aspect;

import com.github.marschall.stiletto.api.advice.After;
import com.github.marschall.stiletto.api.advice.Before;
import com.github.marschall.stiletto.api.injection.MethodName;

public class LoggingAspect {

  @Before
  public void logEntering(@MethodName String methodName) {
    System.out.println("entering " + methodName);
  }

  @After
  public void logExiting(@MethodName String methodName) {
    System.out.println("exiting " + methodName);
  }

}
