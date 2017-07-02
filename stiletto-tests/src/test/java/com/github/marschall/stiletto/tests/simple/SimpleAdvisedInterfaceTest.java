package com.github.marschall.stiletto.tests.simple;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.github.marschall.stiletto.tests.BeforeCountingAspect;

public class SimpleAdvisedInterfaceTest {

  private BeforeCountingAspect aspect;

  @Before
  public void setUp() {
    this.aspect = new BeforeCountingAspect();
  }

  @Test
  public void simpleMethod() {
    SimpleAdvisedInterface targetObject = () -> {
      assertEquals(1, this.aspect.getInvocationCount());
      return "ok";
    };

    SimpleAdvisedInterface_ proxy = new SimpleAdvisedInterface_(targetObject, this.aspect);

    assertEquals(0, this.aspect.getInvocationCount());
    assertEquals("ok", proxy.simpleMethod());
    assertEquals(1, this.aspect.getInvocationCount());
  }

}
