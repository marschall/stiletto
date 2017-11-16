package com.github.marschall.stiletto.tests.constructors;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.github.marschall.stiletto.tests.BeforeCountingAspect;

public class AdvisedServiceWithConstructorsTest {

  @Test
  public void simpleMethodIntConstructor() {
    int value = 42;
    AdvisedServiceWithConstructors targetObject = new AdvisedServiceWithConstructors(value);
    BeforeCountingAspect aspect = new BeforeCountingAspect();
    AdvisedServiceWithConstructors proxy = new AdvisedServiceWithConstructors_(value, targetObject, aspect);

    assertEquals(0, aspect.getInvocationCount());
    assertEquals(Integer.toString(value), proxy.simpleMethod());
    assertEquals(1, aspect.getInvocationCount());
  }

  @Test
  public void simpleMethodLongConstructor() {
    long value = 42;
    AdvisedServiceWithConstructors targetObject = new AdvisedServiceWithConstructors(value);
    BeforeCountingAspect aspect = new BeforeCountingAspect();
    AdvisedServiceWithConstructors proxy = new AdvisedServiceWithConstructors_(value, targetObject, aspect);

    assertEquals(0, aspect.getInvocationCount());
    assertEquals(Long.toString(value), proxy.simpleMethod());
    assertEquals(1, aspect.getInvocationCount());
  }

}
