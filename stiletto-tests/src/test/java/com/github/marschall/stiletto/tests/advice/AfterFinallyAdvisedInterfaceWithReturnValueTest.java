package com.github.marschall.stiletto.tests.advice;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class AfterFinallyAdvisedInterfaceWithReturnValueTest {

  private AfterFinallyAspectWithReturnValue aspect;

  @BeforeEach
  public void setUp() {
    this.aspect = new AfterFinallyAspectWithReturnValue();
  }

  @Test
  public void afterFinally() {

    AfterFinallyAdvisedInterfaceWithReturnValue targetObject = () ->  {
      return "ok";
    };

    AfterFinallyAdvisedInterfaceWithReturnValue proxy = new AfterFinallyAdvisedInterfaceWithReturnValue_(targetObject, aspect);

    assertEquals("called", proxy.simpleMethod());
  }

  @Test
  public void afterFinallyWithException() {

    AfterFinallyAdvisedInterfaceWithReturnValue targetObject = () ->  {
      throw new RuntimeException("abnormal return");
    };

    AfterFinallyAdvisedInterfaceWithReturnValue proxy = new AfterFinallyAdvisedInterfaceWithReturnValue_(targetObject, aspect);

    assertEquals("called", proxy.simpleMethod());
  }

}
