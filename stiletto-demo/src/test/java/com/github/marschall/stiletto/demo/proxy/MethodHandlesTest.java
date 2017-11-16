package com.github.marschall.stiletto.demo.proxy;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

import org.junit.jupiter.api.Test;

public class MethodHandlesTest {

  private static final MethodHandle PRIVATE_METHOD;

  static {
    try {
      PRIVATE_METHOD = MethodHandles.lookup()
        .findSpecial(MethodHandlesTest.class, "privateMethod", MethodType.methodType(Void.TYPE), MethodHandlesTest.class);
    } catch (ReflectiveOperationException e) {
      throw new IllegalStateException("could not find method", e);
    }
  }

  @Test
  public void callMethodHandle() throws Throwable {
    PRIVATE_METHOD.invokeExact(this);
  }

  private void privateMethod() {
    assertTrue(true);
  }

}
