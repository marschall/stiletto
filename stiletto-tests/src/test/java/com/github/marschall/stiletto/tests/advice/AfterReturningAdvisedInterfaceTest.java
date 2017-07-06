package com.github.marschall.stiletto.tests.advice;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

public class AfterReturningAdvisedInterfaceTest {

  private AfterReturningAspect aspect;

  @Before
  public void setUp() {
    this.aspect = new AfterReturningAspect();
  }

  @Test
  public void afterReturning() {

    AfterReturningAdvisedInterface targetObject = () ->  {
      assertEquals(0, this.aspect.getInvocationCount());
      return "ok";
    };

    AfterReturningAdvisedInterface proxy = new AfterReturningAdvisedInterface_(targetObject, aspect);

    assertEquals(0, this.aspect.getInvocationCount());
    assertEquals("ok", proxy.simpleMethod());
    assertEquals(1, this.aspect.getInvocationCount());
  }

  @Test
  public void afterReturningWithException() {

    AfterReturningAdvisedInterface targetObject = () ->  {
      assertEquals(0, this.aspect.getInvocationCount());
      throw new RuntimeException("abnormal return");
    };

    AfterReturningAdvisedInterface proxy = new AfterReturningAdvisedInterface_(targetObject, aspect);

    assertEquals(0, this.aspect.getInvocationCount());
    try {
      proxy.simpleMethod();
      fail("should not reach here");
    } catch (RuntimeException e) {
      // should reach here
    }
    assertEquals(0, this.aspect.getInvocationCount());
  }

}
