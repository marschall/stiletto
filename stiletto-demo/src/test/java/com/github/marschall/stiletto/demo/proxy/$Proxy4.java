package com.github.marschall.stiletto.demo.proxy;


import com.github.marschall.stiletto.demo.proxy.InterfaceProxyTest.SampleInterface;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.lang.reflect.UndeclaredThrowableException;

final class $Proxy4 extends Proxy implements SampleInterface {

  private static Method m1;
  private static Method m2;
  private static Method m3;
  private static Method m0;

  public $Proxy4(InvocationHandler var1)  {
    super(var1);
  }

  public final boolean equals(Object var1) {
    try {
      return (Boolean) super.h.invoke(this, m1, new Object[]{var1});
    } catch (RuntimeException | Error e) {
      throw e;
    } catch (Throwable e) {
      throw new UndeclaredThrowableException(e);
    }
  }

  public final String toString() {
    try {
      return (String) super.h.invoke(this, m2, (Object[]) null);
    } catch (RuntimeException | Error e) {
      throw e;
    } catch (Throwable e) {
      throw new UndeclaredThrowableException(e);
    }
  }

  public final String method() {
    try {
      return (String) super.h.invoke(this, m3, (Object[]) null);
    } catch (RuntimeException | Error e) {
      throw e;
    } catch (Throwable e) {
      throw new UndeclaredThrowableException(e);
    }
  }

  public final int hashCode() {
    try {
      return (Integer) super.h.invoke(this, m0, (Object[]) null);
    } catch (RuntimeException | Error e) {
      throw e;
    } catch (Throwable e) {
      throw new UndeclaredThrowableException(e);
    }
  }

  static {
    try {
      m1 = Class.forName("java.lang.Object").getMethod("equals", new Class[]{Class.forName("java.lang.Object")});
      m2 = Class.forName("java.lang.Object").getMethod("toString", new Class[0]);
      m3 = Class.forName("com.github.marschall.stiletto.demo.proxy.InterfaceProxyTest$SampleInterface").getMethod("method", new Class[0]);
      m0 = Class.forName("java.lang.Object").getMethod("hashCode", new Class[0]);
    } catch (NoSuchMethodException e) {
      throw new NoSuchMethodError(e.getMessage());
    } catch (ClassNotFoundException e) {
      throw new NoClassDefFoundError(e.getMessage());
    }
  }
}

