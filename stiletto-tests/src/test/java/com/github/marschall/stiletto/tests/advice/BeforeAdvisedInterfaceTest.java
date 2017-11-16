package com.github.marschall.stiletto.tests.advice;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class BeforeAdvisedInterfaceTest {

  private BeforeAspect aspect;

  @BeforeEach
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
