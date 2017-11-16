package com.github.marschall.stiletto.tests.advice;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class AfterThrowingAdvisedInterfaceWithReturnValueTest {

  private AfterThrowingAspectWithReturnValue aspect;

  @BeforeEach
  public void setUp() {
    this.aspect = new AfterThrowingAspectWithReturnValue();
  }

  @Test
  public void afterThrowing() {

    AfterThrowingAdvisedInterfaceWithReturnValue targetObject = () ->  {
      return "ok";
    };

    AfterThrowingAdvisedInterfaceWithReturnValue proxy = new AfterThrowingAdvisedInterfaceWithReturnValue_(targetObject, aspect);

    assertEquals("ok", proxy.simpleMethod());
  }

  @Test
  public void afterThrowingAWithException() {

    AfterThrowingAdvisedInterfaceWithReturnValue targetObject = () ->  {
      throw new RuntimeException("abnormal return");
    };

    AfterThrowingAdvisedInterfaceWithReturnValue proxy = new AfterThrowingAdvisedInterfaceWithReturnValue_(targetObject, aspect);

    assertEquals("called", proxy.simpleMethod());
  }

}
