package com.github.marschall.stiletto.tests.advice;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

public class AfterFinallyAdvisedInterfaceTest {

  private AfterFinallyAspect aspect;

  @Before
  public void setUp() {
    this.aspect = new AfterFinallyAspect();
  }

  @Test
  public void afterFinally() {

    AfterFinallyAdvisedInterface targetObject = () ->  {
      assertEquals(0, this.aspect.getInvocationCount());
      return "ok";
    };

    AfterFinallyAdvisedInterface proxy = new AfterFinallyAdvisedInterface_(targetObject, aspect);

    assertEquals(0, this.aspect.getInvocationCount());
    assertEquals("ok", proxy.simpleMethod());
    assertEquals(1, this.aspect.getInvocationCount());
  }

  @Test
  public void afterFinallyWithException() {

    AfterFinallyAdvisedInterface targetObject = () ->  {
      assertEquals(0, this.aspect.getInvocationCount());
      throw new RuntimeException("abnormal return");
    };

    AfterFinallyAdvisedInterface proxy = new AfterFinallyAdvisedInterface_(targetObject, aspect);

    assertEquals(0, this.aspect.getInvocationCount());
    try {
      proxy.simpleMethod();
      fail("should not reach here");
    } catch (RuntimeException e) {
      // should reach here
    }
    assertEquals(1, this.aspect.getInvocationCount());
  }

}
