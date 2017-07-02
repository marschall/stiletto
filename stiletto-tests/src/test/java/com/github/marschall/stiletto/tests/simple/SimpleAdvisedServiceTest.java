package com.github.marschall.stiletto.tests.simple;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.github.marschall.stiletto.tests.BeforeCountingAspect;

public class SimpleAdvisedServiceTest {

  private SimpleAdvisedService proxy;
  private BeforeCountingAspect aspect;

  @Before
  public void setUp() {
    SimpleAdvisedService targetObject = new SimpleAdvisedService();
    this.aspect = new BeforeCountingAspect();
    this.proxy = new SimpleAdvisedService_(targetObject, this.aspect);
  }

  @Test
  public void simpleMethod() {
    assertEquals(0, this.aspect.getInvocationCount());
    assertEquals("ok", this.proxy.simpleMethod());
    assertEquals(1, this.aspect.getInvocationCount());
  }

}
