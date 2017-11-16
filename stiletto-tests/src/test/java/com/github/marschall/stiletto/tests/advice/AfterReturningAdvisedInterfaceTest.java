package com.github.marschall.stiletto.tests.advice;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class AfterReturningAdvisedInterfaceTest {

  private AfterReturningAspect aspect;

  @BeforeEach
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
