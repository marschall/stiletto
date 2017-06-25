package com.github.marschall.stiletto.tests.constructors;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.github.marschall.stiletto.tests.BeforeCountingAspect;

public class AdvisedServiceWithConstructorsTest {

  private AdvisedServiceWithConstructors proxy;
  private BeforeCountingAspect aspect;

  @Before
  public void setUp() {
    AdvisedServiceWithConstructors targetObject = new AdvisedServiceWithConstructors(42);
    this.aspect = new BeforeCountingAspect();
    this.proxy = new AdvisedServiceWithConstructors_(42, targetObject, this.aspect);
  }

  @Test
  public void simpleMethod() {
    assertEquals(0, this.aspect.getInvocationCount());
    assertEquals("42", this.proxy.simpleMethod());
    assertEquals(1, this.aspect.getInvocationCount());
  }

}
