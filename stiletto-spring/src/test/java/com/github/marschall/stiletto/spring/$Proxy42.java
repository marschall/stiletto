package com.github.marschall.stiletto.spring;

import com.github.marschall.stiletto.spring.SimpleCacheableInterface;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.lang.reflect.UndeclaredThrowableException;
import org.aopalliance.aop.Advice;
import org.springframework.aop.Advisor;
import org.springframework.aop.SpringProxy;
import org.springframework.aop.TargetSource;
import org.springframework.aop.framework.Advised;
import org.springframework.aop.framework.AopConfigException;
import org.springframework.core.DecoratingProxy;

public final class $Proxy42 extends Proxy implements SimpleCacheableInterface, SpringProxy, Advised, DecoratingProxy {

  private static Method m1;
  private static Method m12;
  private static Method m16;
  private static Method m11;
  private static Method m24;
  private static Method m20;
  private static Method m6;
  private static Method m10;
  private static Method m18;
  private static Method m17;
  private static Method m0;
  private static Method m21;
  private static Method m26;
  private static Method m15;
  private static Method m3;
  private static Method m9;
  private static Method m2;
  private static Method m28;
  private static Method m4;
  private static Method m13;
  private static Method m29;
  private static Method m23;
  private static Method m7;
  private static Method m8;
  private static Method m25;
  private static Method m14;
  private static Method m27;
  private static Method m22;
  private static Method m5;
  private static Method m19;

  public $Proxy42(InvocationHandler h) {
    super(h);
  }

  public final boolean equals(Object obj) {
    try {
      return (Boolean) super.h.invoke(this, m1, new Object[] { obj });
    } catch (RuntimeException | Error e) {
      throw e;
    } catch (Throwable e) {
      throw new UndeclaredThrowableException(e);
    }
  }

  public final void addAdvisor(Advisor var1) throws AopConfigException {
    try {
      super.h.invoke(this, m12, new Object[] { var1 });
    } catch (RuntimeException | Error e) {
      throw e;
    } catch (Throwable e) {
      throw new UndeclaredThrowableException(e);
    }
  }

  public final boolean isExposeProxy() {
    try {
      return (Boolean) super.h.invoke(this, m16, (Object[]) null);
    } catch (RuntimeException | Error e) {
      throw e;
    } catch (Throwable e) {
      throw new UndeclaredThrowableException(e);
    }
  }

  public final boolean isProxyTargetClass() {
    try {
      return (Boolean) super.h.invoke(this, m11, (Object[]) null);
    } catch (RuntimeException | Error var2) {
      throw var2;
    } catch (Throwable var3) {
      throw new UndeclaredThrowableException(var3);
    }
  }

  public final void removeAdvisor(int var1) throws AopConfigException {
    try {
      super.h.invoke(this, m24, new Object[] { var1 });
    } catch (RuntimeException | Error e) {
      throw e;
    } catch (Throwable e) {
      throw new UndeclaredThrowableException(e);
    }
  }

  public final Class<?>[] getProxiedInterfaces() {
    try {
      return (Class[]) super.h.invoke(this, m20, (Object[]) null);
    } catch (RuntimeException | Error e) {
      throw e;
    } catch (Throwable e) {
      throw new UndeclaredThrowableException(e);
    }
  }

  public final int indexOf(Advisor var1) {
    try {
      return (Integer) super.h.invoke(this, m6, new Object[] { var1 });
    } catch (RuntimeException | Error e) {
      throw e;
    } catch (Throwable e) {
      throw new UndeclaredThrowableException(e);
    }
  }

  public final TargetSource getTargetSource() {
    try {
      return (TargetSource) super.h.invoke(this, m10, (Object[]) null);
    } catch (RuntimeException | Error e) {
      throw e;
    } catch (Throwable e) {
      throw new UndeclaredThrowableException(e);
    }
  }

  public final void addAdvice(int pos, Advice advice) throws AopConfigException {
    try {
      super.h.invoke(this, m18, new Object[] { pos, advice });
    } catch (RuntimeException | Error e) {
      throw e;
    } catch (Throwable e) {
      throw new UndeclaredThrowableException(e);
    }
  }

  public final void addAdvice(Advice advice) throws AopConfigException {
    try {
      super.h.invoke(this, m17, new Object[] { advice });
    } catch (RuntimeException | Error e) {
      throw e;
    } catch (Throwable e) {
      throw new UndeclaredThrowableException(e);
    }
  }

  public final int hashCode() {
    try {
      return ((Integer) super.h.invoke(this, m0, (Object[]) null)).intValue();
    } catch (RuntimeException | Error e) {
      throw e;
    } catch (Throwable e) {
      throw new UndeclaredThrowableException(e);
    }
  }

  public final boolean isInterfaceProxied(Class<?> var1) {
    try {
      return (Boolean) super.h.invoke(this, m21, new Object[] { var1 });
    } catch (RuntimeException | Error e) {
      throw e;
    } catch (Throwable e) {
      throw new UndeclaredThrowableException(e);
    }
  }

  public final boolean removeAdvice(Advice advice) {
    try {
      return (Boolean) super.h.invoke(this, m26, new Object[] { advice });
    } catch (RuntimeException | Error e) {
      throw e;
    } catch (Throwable e) {
      throw new UndeclaredThrowableException(e);
    }
  }

  public final void setExposeProxy(boolean exposeProxy) {
    try {
      super.h.invoke(this, m15, new Object[] { exposeProxy });
    } catch (RuntimeException | Error e) {
      throw e;
    } catch (Throwable e) {
      throw new UndeclaredThrowableException(e);
    }
  }

  public final void cacheEvict(Integer i) {
    try {
      super.h.invoke(this, m3, new Object[] { i });
    } catch (RuntimeException | Error e) {
      throw e;
    } catch (Throwable e) {
      throw new UndeclaredThrowableException(e);
    }
  }

  public final void setTargetSource(TargetSource targetSource) {
    try {
      super.h.invoke(this, m9, new Object[] { targetSource });
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

  public final Class<?> getTargetClass() {
    try {
      return (Class<?>) super.h.invoke(this, m28, (Object[]) null);
    } catch (RuntimeException | Error e) {
      throw e;
    } catch (Throwable e) {
      throw new UndeclaredThrowableException(e);
    }
  }

  public final void cachePut(Long l) {
    try {
      super.h.invoke(this, m4, new Object[] { l });
    } catch (RuntimeException | Error e) {
      throw e;
    } catch (Throwable e) {
      throw new UndeclaredThrowableException(e);
    }
  }

  public final void addAdvisor(int pos, Advisor advisor) throws AopConfigException {
    try {
      super.h.invoke(this, m13, new Object[] { pos, advisor });
    } catch (RuntimeException | Error e) {
      throw e;
    } catch (Throwable e) {
      throw new UndeclaredThrowableException(e);
    }
  }

  public final Class<?> getDecoratedClass() {
    try {
      return (Class<?>) super.h.invoke(this, m29, (Object[]) null);
    } catch (RuntimeException | Error e) {
      throw e;
    } catch (Throwable e) {
      throw new UndeclaredThrowableException(e);
    }
  }

  public final boolean removeAdvisor(Advisor advisor) {
    try {
      return (Boolean) super.h.invoke(this, m23, new Object[] { advisor });
    } catch (RuntimeException | Error e) {
      throw e;
    } catch (Throwable e) {
      throw new UndeclaredThrowableException(e);
    }
  }

  public final int indexOf(Advice var1) {
    try {
      return (Integer) super.h.invoke(this, m7, new Object[] { var1 });
    } catch (RuntimeException | Error e) {
      throw e;
    } catch (Throwable e) {
      throw new UndeclaredThrowableException(e);
    }
  }

  public final boolean isFrozen() {
    try {
      return (Boolean) super.h.invoke(this, m8, (Object[]) null);
    } catch (RuntimeException | Error e) {
      throw e;
    } catch (Throwable e) {
      throw new UndeclaredThrowableException(e);
    }
  }

  public final boolean replaceAdvisor(Advisor var1, Advisor var2) throws AopConfigException {
    try {
      return (Boolean) super.h.invoke(this, m25, new Object[] { var1, var2 });
    } catch (RuntimeException | Error e) {
      throw e;
    } catch (Throwable e) {
      throw new UndeclaredThrowableException(e);
    }
  }

  public final void setPreFiltered(boolean preFiltered) {
    try {
      super.h.invoke(this, m14, new Object[] { preFiltered });
    } catch (RuntimeException | Error e) {
      throw e;
    } catch (Throwable e) {
      throw new UndeclaredThrowableException(e);
    }
  }

  public final String toProxyConfigString() {
    try {
      return (String) super.h.invoke(this, m27, (Object[]) null);
    } catch (RuntimeException | Error e) {
      throw e;
    } catch (Throwable e) {
      throw new UndeclaredThrowableException(e);
    }
  }

  public final Advisor[] getAdvisors() {
    try {
      return (Advisor[]) super.h.invoke(this, m22, (Object[]) null);
    } catch (RuntimeException | Error e) {
      throw e;
    } catch (Throwable e) {
      throw new UndeclaredThrowableException(e);
    }
  }

  public final String cacheable(String s) {
    try {
      return (String) super.h.invoke(this, m5, new Object[] { s });
    } catch (RuntimeException | Error e) {
      throw e;
    } catch (Throwable e) {
      throw new UndeclaredThrowableException(e);
    }
  }

  public final boolean isPreFiltered() {
    try {
      return (Boolean) super.h.invoke(this, m19, (Object[]) null);
    } catch (RuntimeException | Error e) {
      throw e;
    } catch (Throwable e) {
      throw new UndeclaredThrowableException(e);
    }
  }

  static {
    try {
      m1 = Class.forName("java.lang.Object").getMethod("equals", Class.forName("java.lang.Object"));
      m12 = Class.forName("org.springframework.aop.framework.Advised").getMethod("addAdvisor", Class.forName("org.springframework.aop.Advisor"));
      m16 = Class.forName("org.springframework.aop.framework.Advised").getMethod("isExposeProxy");
      m11 = Class.forName("org.springframework.aop.framework.Advised").getMethod("isProxyTargetClass");
      m24 = Class.forName("org.springframework.aop.framework.Advised").getMethod("removeAdvisor", Integer.TYPE);
      m20 = Class.forName("org.springframework.aop.framework.Advised").getMethod("getProxiedInterfaces");
      m6 = Class.forName("org.springframework.aop.framework.Advised").getMethod("indexOf", Class.forName("org.springframework.aop.Advisor"));
      m10 = Class.forName("org.springframework.aop.framework.Advised").getMethod("getTargetSource");
      m18 = Class.forName("org.springframework.aop.framework.Advised").getMethod("addAdvice", Integer.TYPE, Class.forName("org.aopalliance.aop.Advice"));
      m17 = Class.forName("org.springframework.aop.framework.Advised").getMethod("addAdvice", Class.forName("org.aopalliance.aop.Advice"));
      m0 = Class.forName("java.lang.Object").getMethod("hashCode");
      m21 = Class.forName("org.springframework.aop.framework.Advised").getMethod("isInterfaceProxied", Class.forName("java.lang.Class"));
      m26 = Class.forName("org.springframework.aop.framework.Advised").getMethod("removeAdvice", Class.forName("org.aopalliance.aop.Advice"));
      m15 = Class.forName("org.springframework.aop.framework.Advised").getMethod("setExposeProxy", Boolean.TYPE);
      m3 = Class.forName("com.github.marschall.stiletto.spring.SimpleCacheableInterface").getMethod("cacheEvict", Class.forName("java.lang.Integer"));
      m9 = Class.forName("org.springframework.aop.framework.Advised").getMethod("setTargetSource", Class.forName("org.springframework.aop.TargetSource"));
      m2 = Class.forName("java.lang.Object").getMethod("toString");
      m28 = Class.forName("org.springframework.aop.framework.Advised").getMethod("getTargetClass");
      m4 = Class.forName("com.github.marschall.stiletto.spring.SimpleCacheableInterface").getMethod("cachePut", Class.forName("java.lang.Long"));
      m13 = Class.forName("org.springframework.aop.framework.Advised").getMethod("addAdvisor", Integer.TYPE, Class.forName("org.springframework.aop.Advisor"));
      m29 = Class.forName("org.springframework.core.DecoratingProxy").getMethod("getDecoratedClass");
      m23 = Class.forName("org.springframework.aop.framework.Advised").getMethod("removeAdvisor", Class.forName("org.springframework.aop.Advisor"));
      m7 = Class.forName("org.springframework.aop.framework.Advised").getMethod("indexOf", Class.forName("org.aopalliance.aop.Advice"));
      m8 = Class.forName("org.springframework.aop.framework.Advised").getMethod("isFrozen");
      m25 = Class.forName("org.springframework.aop.framework.Advised").getMethod("replaceAdvisor", Class.forName("org.springframework.aop.Advisor"), Class.forName("org.springframework.aop.Advisor"));
      m14 = Class.forName("org.springframework.aop.framework.Advised").getMethod("setPreFiltered", Boolean.TYPE);
      m27 = Class.forName("org.springframework.aop.framework.Advised").getMethod("toProxyConfigString");
      m22 = Class.forName("org.springframework.aop.framework.Advised").getMethod("getAdvisors");
      m5 = Class.forName("com.github.marschall.stiletto.spring.SimpleCacheableInterface").getMethod("cacheable", Class.forName("java.lang.String"));
      m19 = Class.forName("org.springframework.aop.framework.Advised").getMethod("isPreFiltered");
    } catch (NoSuchMethodException e) {
      throw new NoSuchMethodError(e.getMessage());
    } catch (ClassNotFoundException e) {
      throw new NoClassDefFoundError(e.getMessage());
    }
  }
}
