package com.github.marschall.stiletto.tests.advice;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

public class AfterThrowingAdvisedInterfaceWithReturnValueTest {

  private AfterThrowingAspectWithReturnValue aspect;

  @Before
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
