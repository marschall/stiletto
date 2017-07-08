package com.github.marschall.stiletto.tests.injection;

import static org.junit.Assert.*;

import org.junit.Test;

public class InjectReturnValueTest {

  @Test
  public void noArgument() {
    CaptureReturnValueAspect aspect = new CaptureReturnValueAspect();
    InjectReturnValue targetObject = () -> "return value";
    InjectReturnValue proxy = new InjectReturnValue_(targetObject, aspect);

    assertEquals("return value", proxy.method());
    assertEquals("return value", aspect.getReturnValue());
  }

}
