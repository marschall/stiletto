package com.github.marschall.stiletto.tests.generic;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.github.marschall.stiletto.tests.BeforeCountingAspect;

public class AdvisedGenericInterfaceTest {

  private BeforeCountingAspect aspect;

  @BeforeEach
  public void setUp() {
    this.aspect = new BeforeCountingAspect();
  }

  @Test
  public void genericMethod() {

    AdvisedGenericInterface<String> targetObject = () ->  {
      assertEquals(1, this.aspect.getInvocationCount());
      return "ok";
    };

    AdvisedGenericInterface<String> proxy = new AdvisedGenericInterface_<>(targetObject, this.aspect);

    assertEquals(0, this.aspect.getInvocationCount());
    assertEquals("ok", proxy.genericMethod());
    assertEquals(1, this.aspect.getInvocationCount());
  }

}
