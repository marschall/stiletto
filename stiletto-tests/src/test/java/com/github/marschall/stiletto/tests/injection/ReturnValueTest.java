package com.github.marschall.stiletto.tests.injection;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ReturnValueTest {

  @Test
  public void noArgument() {
    CaptureReturnValueAspect aspect = new CaptureReturnValueAspect();
    InjectReturnValue targetObject = () -> "return value";
    InjectReturnValue proxy = new InjectReturnValue_(targetObject, aspect);

    assertEquals("return value", proxy.method());
    assertEquals("return value", aspect.getReturnValue());
  }

}
