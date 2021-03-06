package com.github.marschall.stiletto.tests.injection;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class ReturnValueTest {

  @Test
  public void returnValue() {
    CaptureReturnValueAspect aspect = new CaptureReturnValueAspect();
    InjectReturnValue targetObject = () -> "return value";
    InjectReturnValue proxy = new InjectReturnValue_(targetObject, aspect);

    assertEquals("return value", proxy.method());
    assertEquals("return value", aspect.getReturnValue());
  }

}
