package com.github.marschall.stiletto.tests.simple;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.github.marschall.stiletto.tests.BeforeCountingAspect;

public class SimpleAdvisedInterfaceTest {

  private SimpleAdvisedInterface proxy;
  private BeforeCountingAspect aspect;

  @Before
  public void setUp() {
    SimpleAdvisedInterface targetObject = () -> "ok";
    this.aspect = new BeforeCountingAspect();
    this.proxy = new SimpleAdvisedInterface_(targetObject, this.aspect);
  }

  @Test
  public void simpleMethod() {
    assertEquals(0, this.aspect.getInvocationCount());
    assertEquals("ok", this.proxy.simpleMethod());
    assertEquals(1, this.aspect.getInvocationCount());
  }

}
