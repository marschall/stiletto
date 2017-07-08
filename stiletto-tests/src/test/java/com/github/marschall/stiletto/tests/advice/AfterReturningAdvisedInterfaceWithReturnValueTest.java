package com.github.marschall.stiletto.tests.advice;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

public class AfterReturningAdvisedInterfaceWithReturnValueTest {

  private AfterReturningAspectWithReturnValue aspect;

  @Before
  public void setUp() {
    this.aspect = new AfterReturningAspectWithReturnValue();
  }

  @Test
  public void afterReturning() {

    AfterReturningAdvisedInterfaceWithReturnValue targetObject = () ->  {
      return "ok";
    };

    AfterReturningAdvisedInterfaceWithReturnValue proxy = new AfterReturningAdvisedInterfaceWithReturnValue_(targetObject, aspect);

    assertEquals("called", proxy.simpleMethod());
  }

  @Test
  public void afterReturningWithException() {

    AfterReturningAdvisedInterfaceWithReturnValue targetObject = () ->  {
      throw new RuntimeException("abnormal return");
    };

    AfterReturningAdvisedInterfaceWithReturnValue proxy = new AfterReturningAdvisedInterfaceWithReturnValue_(targetObject, aspect);

    try {
      proxy.simpleMethod();
      fail("should not reach here");
    } catch (RuntimeException e) {
      // should reach here
    }
  }

}
