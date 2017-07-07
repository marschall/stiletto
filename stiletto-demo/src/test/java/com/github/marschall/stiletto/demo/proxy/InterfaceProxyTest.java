package com.github.marschall.stiletto.demo.proxy;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.junit.Test;

public class InterfaceProxyTest {

  @Test
  public void test() throws ReflectiveOperationException {
    InvocationHandler handler = new NoOpInvocationHandler();
    SampleInterface proxy = (SampleInterface) Proxy.newProxyInstance(InterfaceProxyTest.class.getClassLoader(), new Class[] {SampleInterface.class}, handler);
    assertEquals("ok", proxy.method());

    SampleAnnotation annotation = SampleInterface.class.getDeclaredMethod("method").getAnnotation(SampleAnnotation.class);
    assertNotNull(annotation);
    assertEquals("demo", annotation.value());
  }

  interface SampleInterface {

    @SampleAnnotation("demo")
    String method();

  }

  @Retention(RUNTIME)
  @Target(METHOD)
  @interface SampleAnnotation {

    String value();

  }

  static final class NoOpInvocationHandler implements InvocationHandler {

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
      return "ok";
    }

  }

}
