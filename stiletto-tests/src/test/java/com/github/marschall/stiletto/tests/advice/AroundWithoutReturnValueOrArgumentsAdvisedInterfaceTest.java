package com.github.marschall.stiletto.tests.advice;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class AroundWithoutReturnValueOrArgumentsAdvisedInterfaceTest {

  @Test
  public void simpleMethodIntConstructor() {
    AroundWithoutReturnValueOrArgumentsAdvisedInterface targetObject = s -> s;
    AroundWithoutReturnValueOrArgumentsAspect aspect =
            new AroundWithoutReturnValueOrArgumentsAspect();
    AroundWithoutReturnValueOrArgumentsAdvisedInterface proxy =
            new AroundWithoutReturnValueOrArgumentsAdvisedInterface_(targetObject, aspect);

    assertFalse(aspect.isBeforePassed());
    assertFalse(aspect.isAfterPassed());
    assertEquals("x", proxy.simpleMethod("x"));
    assertTrue(aspect.isBeforePassed());
    assertTrue(aspect.isAfterPassed());
  }

}
