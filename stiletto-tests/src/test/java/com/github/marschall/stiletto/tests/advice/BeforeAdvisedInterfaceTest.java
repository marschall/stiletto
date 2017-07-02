package com.github.marschall.stiletto.tests.advice;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

public class BeforeAdvisedInterfaceTest {

  private BeforeAspect aspect;

  @Before
  public void setUp() {
    this.aspect = new BeforeAspect();
  }

  @Test
  public void before() {

    BeforeAdvisedInterface targetObject = () ->  {
      assertEquals(1, this.aspect.getInvocationCount());
      return "ok";
    };

    BeforeAdvisedInterface proxy = new BeforeAdvisedInterface_(targetObject, aspect);

    assertEquals(0, this.aspect.getInvocationCount());
    assertEquals("ok", proxy.simpleMethod());
    assertEquals(1, this.aspect.getInvocationCount());
  }

}
