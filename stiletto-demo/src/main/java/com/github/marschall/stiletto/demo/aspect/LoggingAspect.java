package com.github.marschall.stiletto.demo.aspect;

import com.github.marschall.stiletto.api.injection.MethodName;
import com.github.marschall.stiletto.api.pointcuts.After;
import com.github.marschall.stiletto.api.pointcuts.Before;

public class LoggingAspect {

  @Before
  void logEntering(@MethodName String methodName) {
    System.out.println("entering " + methodName);
  }

  @After
  void logExiting(@MethodName String methodName) {
    System.out.println("exiting " + methodName);
  }

}
