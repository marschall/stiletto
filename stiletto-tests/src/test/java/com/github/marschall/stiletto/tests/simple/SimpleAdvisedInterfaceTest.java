package com.github.marschall.stiletto.tests.simple;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.github.marschall.stiletto.tests.BeforeCountingAspect;

public class SimpleAdvisedInterfaceTest {

  private BeforeCountingAspect aspect;

  @BeforeEach
  public void setUp() {
    this.aspect = new BeforeCountingAspect();
  }

  @Test
  public void simpleMethod() {
    SimpleAdvisedInterface targetObject = () -> {
      assertEquals(1, this.aspect.getInvocationCount());
      return "ok";
    };

    SimpleAdvisedInterface proxy = new SimpleAdvisedInterface_(targetObject, this.aspect);

    assertEquals(0, this.aspect.getInvocationCount());
    assertEquals("ok", proxy.simpleMethod());
    assertEquals(1, this.aspect.getInvocationCount());
  }

}
