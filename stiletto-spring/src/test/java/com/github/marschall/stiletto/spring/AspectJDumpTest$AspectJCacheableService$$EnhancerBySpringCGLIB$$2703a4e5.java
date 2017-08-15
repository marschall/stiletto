package com.github.marschall.stiletto.spring;

import java.lang.reflect.Method;
import java.lang.reflect.UndeclaredThrowableException;

import org.aopalliance.aop.Advice;
import org.springframework.aop.Advisor;
import org.springframework.aop.SpringProxy;
import org.springframework.aop.TargetClassAware;
import org.springframework.aop.TargetSource;
import org.springframework.aop.framework.Advised;
import org.springframework.aop.framework.AopConfigException;
import org.springframework.cglib.core.ReflectUtils;
import org.springframework.cglib.core.Signature;
import org.springframework.cglib.proxy.Callback;
import org.springframework.cglib.proxy.Dispatcher;
import org.springframework.cglib.proxy.Factory;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;
import org.springframework.cglib.proxy.NoOp;

import com.github.marschall.stiletto.spring.AspectJDumpTest.AspectJCacheableService;

public class AspectJDumpTest$AspectJCacheableService$$EnhancerBySpringCGLIB$$2703a4e5 extends AspectJCacheableService implements SpringProxy, Advised, Factory {
  private boolean CGLIB$BOUND;
  public static Object CGLIB$FACTORY_DATA;
  private static /* final */ ThreadLocal<Callback[]> CGLIB$THREAD_CALLBACKS;
  private static /* final */ Callback[] CGLIB$STATIC_CALLBACKS;
  private MethodInterceptor CGLIB$CALLBACK_0;
  private MethodInterceptor CGLIB$CALLBACK_1;
  private NoOp CGLIB$CALLBACK_2;
  private Dispatcher CGLIB$CALLBACK_3;
  private Dispatcher CGLIB$CALLBACK_4;
  private MethodInterceptor CGLIB$CALLBACK_5;
  private MethodInterceptor CGLIB$CALLBACK_6;
  private static Object CGLIB$CALLBACK_FILTER;
  private static /* final */ Method CGLIB$cacheable$0$Method;
  private static /* final */ MethodProxy CGLIB$cacheable$0$Proxy;
  private static /* final */ Object[] CGLIB$emptyArgs;
  private static /* final */ Method CGLIB$cachePut$1$Method;
  private static /* final */ MethodProxy CGLIB$cachePut$1$Proxy;
  private static /* final */ Method CGLIB$cacheEvict$2$Method;
  private static /* final */ MethodProxy CGLIB$cacheEvict$2$Proxy;
  private static /* final */ Method CGLIB$equals$3$Method;
  private static /* final */ MethodProxy CGLIB$equals$3$Proxy;
  private static /* final */ Method CGLIB$toString$4$Method;
  private static /* final */ MethodProxy CGLIB$toString$4$Proxy;
  private static /* final */ Method CGLIB$hashCode$5$Method;
  private static /* final */ MethodProxy CGLIB$hashCode$5$Proxy;
  private static /* final */ Method CGLIB$clone$6$Method;
  private static /* final */ MethodProxy CGLIB$clone$6$Proxy;

  static void CGLIB$STATICHOOK5() {
    CGLIB$THREAD_CALLBACKS = new ThreadLocal<>();
    CGLIB$emptyArgs = new Object[0];
    try {
      Class<?> proxyClass = Class.forName(
              "com.github.marschall.stiletto.spring.AspectJDumpTest$AspectJCacheableService$$EnhancerBySpringCGLIB$$2703a4e5");
      Class<?> lookupClass;
      Method[] var10000 = ReflectUtils.findMethods(
              new String[] {
                  "equals", "(Ljava/lang/Object;)Z",
                  "toString", "()Ljava/lang/String;",
                  "hashCode", "()I",
                  "clone", "()Ljava/lang/Object;" },
              (lookupClass = Class.forName("java.lang.Object")).getDeclaredMethods());
      CGLIB$equals$3$Method = var10000[0];
      CGLIB$equals$3$Proxy = MethodProxy.create(lookupClass, proxyClass, "(Ljava/lang/Object;)Z", "equals", "CGLIB$equals$3");
      CGLIB$toString$4$Method = var10000[1];
      CGLIB$toString$4$Proxy = MethodProxy.create(lookupClass, proxyClass, "()Ljava/lang/String;", "toString", "CGLIB$toString$4");
      CGLIB$hashCode$5$Method = var10000[2];
      CGLIB$hashCode$5$Proxy = MethodProxy.create(lookupClass, proxyClass, "()I", "hashCode", "CGLIB$hashCode$5");
      CGLIB$clone$6$Method = var10000[3];
      CGLIB$clone$6$Proxy = MethodProxy.create(lookupClass, proxyClass, "()Ljava/lang/Object;", "clone", "CGLIB$clone$6");
      var10000 = ReflectUtils.findMethods(new String[] {
          "cacheable", "(Ljava/lang/String;)Ljava/lang/String;",
          "cachePut", "(Ljava/lang/Long;)V",
          "cacheEvict", "(Ljava/lang/Integer;)V" },
              (lookupClass = Class.forName("com.github.marschall.stiletto.spring.AspectJDumpTest$AspectJCacheableService")).getDeclaredMethods());
      CGLIB$cacheable$0$Method = var10000[0];
      CGLIB$cacheable$0$Proxy = MethodProxy.create(lookupClass, proxyClass,
              "(Ljava/lang/String;)Ljava/lang/String;", "cacheable",
              "CGLIB$cacheable$0");
      CGLIB$cachePut$1$Method = var10000[1];
      CGLIB$cachePut$1$Proxy = MethodProxy.create(lookupClass, proxyClass, "(Ljava/lang/Long;)V", "cachePut", "CGLIB$cachePut$1");
      CGLIB$cacheEvict$2$Method = var10000[2];
      CGLIB$cacheEvict$2$Proxy = MethodProxy.create(lookupClass, proxyClass,
              "(Ljava/lang/Integer;)V", "cacheEvict", "CGLIB$cacheEvict$2");
    } catch (ClassNotFoundException e) {
      throw new RuntimeException("coule not load class", e);
    }
  }

  final String CGLIB$cacheable$0(String s) {
    return super.cacheable(s);
  }

  public final String cacheable(String s) {
    try {
      MethodInterceptor interceptor = this.CGLIB$CALLBACK_0;
      if (this.CGLIB$CALLBACK_0 == null) {
        CGLIB$BIND_CALLBACKS(this);
        interceptor = this.CGLIB$CALLBACK_0;
      }

      if (interceptor != null) {
        return (String) interceptor.intercept(this, CGLIB$cacheable$0$Method, new Object[] { s }, CGLIB$cacheable$0$Proxy);
      } else {
        return super.cacheable(s);
      }
    } catch (Error | RuntimeException e) {
      throw e;
    } catch (Throwable e) {
      throw new UndeclaredThrowableException(e);
    }
  }

  final void CGLIB$cachePut$1(Long var1) {
    super.cachePut(var1);
  }

  public final void cachePut(Long l) {
    try {
      MethodInterceptor interceptor = this.CGLIB$CALLBACK_0;
      if (this.CGLIB$CALLBACK_0 == null) {
        CGLIB$BIND_CALLBACKS(this);
        interceptor = this.CGLIB$CALLBACK_0;
      }

      if (interceptor != null) {
        interceptor.intercept(this, CGLIB$cachePut$1$Method, new Object[] { l }, CGLIB$cachePut$1$Proxy);
      } else {
        super.cachePut(l);
      }
    } catch (Error | RuntimeException e) {
      throw e;
    } catch (Throwable e) {
      throw new UndeclaredThrowableException(e);
    }
  }

  final void CGLIB$cacheEvict$2(Integer var1) {
    super.cacheEvict(var1);
  }

  public final void cacheEvict(Integer i) {
    try {
      MethodInterceptor interceptor = this.CGLIB$CALLBACK_0;
      if (this.CGLIB$CALLBACK_0 == null) {
        CGLIB$BIND_CALLBACKS(this);
        interceptor = this.CGLIB$CALLBACK_0;
      }

      if (interceptor != null) {
        interceptor.intercept(this, CGLIB$cacheEvict$2$Method, new Object[] { i }, CGLIB$cacheEvict$2$Proxy);
      } else {
        super.cacheEvict(i);
      }
    } catch (Error | RuntimeException e) {
      throw e;
    } catch (Throwable e) {
      throw new UndeclaredThrowableException(e);
    }
  }

  final boolean CGLIB$equals$3(Object var1) {
    return super.equals(var1);
  }

  public final boolean equals(Object obj) {
    try {
      MethodInterceptor interceptor = this.CGLIB$CALLBACK_5;
      if (this.CGLIB$CALLBACK_5 == null) {
        CGLIB$BIND_CALLBACKS(this);
        interceptor = this.CGLIB$CALLBACK_5;
      }

      if (interceptor != null) {
        Object var4 = interceptor.intercept(this, CGLIB$equals$3$Method, new Object[] { obj }, CGLIB$equals$3$Proxy);
        return var4 == null ? false : (Boolean) var4;
      } else {
        return super.equals(obj);
      }
    } catch (Error | RuntimeException e) {
      throw e;
    } catch (Throwable e) {
      throw new UndeclaredThrowableException(e);
    }
  }

  final String CGLIB$toString$4() {
    return super.toString();
  }

  public final String toString() {
    try {
      MethodInterceptor interceptor = this.CGLIB$CALLBACK_0;
      if (this.CGLIB$CALLBACK_0 == null) {
        CGLIB$BIND_CALLBACKS(this);
        interceptor = this.CGLIB$CALLBACK_0;
      }

      if (interceptor != null) {
        return (String) interceptor.intercept(this, CGLIB$toString$4$Method, CGLIB$emptyArgs, CGLIB$toString$4$Proxy);

      } else {
        return super.toString();
      }
    } catch (Error | RuntimeException e) {
      throw e;
    } catch (Throwable e) {
      throw new UndeclaredThrowableException(e);
    }
  }

  final int CGLIB$hashCode$5() {
    return super.hashCode();
  }

  public final int hashCode() {
    try {
      MethodInterceptor interceptor = this.CGLIB$CALLBACK_6;
      if (this.CGLIB$CALLBACK_6 == null) {
        CGLIB$BIND_CALLBACKS(this);
        interceptor = this.CGLIB$CALLBACK_6;
      }

      if (interceptor != null) {
        Object var3 = interceptor.intercept(this, CGLIB$hashCode$5$Method, CGLIB$emptyArgs, CGLIB$hashCode$5$Proxy);
        return var3 == null ? 0 : ((Number) var3).intValue();
      } else {
        return super.hashCode();
      }
    } catch (Error | RuntimeException e) {
      throw e;
    } catch (Throwable e) {
      throw new UndeclaredThrowableException(e);
    }
  }

  final Object CGLIB$clone$6() throws CloneNotSupportedException {
    return super.clone();
  }

  protected final Object clone() throws CloneNotSupportedException {
    try {
      MethodInterceptor interceptor = this.CGLIB$CALLBACK_0;
      if (this.CGLIB$CALLBACK_0 == null) {
        CGLIB$BIND_CALLBACKS(this);
        interceptor = this.CGLIB$CALLBACK_0;
      }

      if (interceptor != null) {
        return interceptor.intercept(this, CGLIB$clone$6$Method, CGLIB$emptyArgs, CGLIB$clone$6$Proxy);
      } else {
        return super.clone();
      }
    } catch (Error | CloneNotSupportedException | RuntimeException e) {
      throw e;
    } catch (Throwable e) {
      throw new UndeclaredThrowableException(e);
    }
  }

  public static MethodProxy CGLIB$findMethodProxy(Signature sig) {
    String signature = sig.toString();
    switch (signature.hashCode()) {
      case -1112830115:
        if (signature.equals("cacheable(Ljava/lang/String;)Ljava/lang/String;")) {
          return CGLIB$cacheable$0$Proxy;
        }
        break;
      case -673782903:
        if (signature.equals("cacheEvict(Ljava/lang/Integer;)V")) {
          return CGLIB$cacheEvict$2$Proxy;
        }
        break;
      case -508378822:
        if (signature.equals("clone()Ljava/lang/Object;")) {
          return CGLIB$clone$6$Proxy;
        }
        break;
      case 1567110247:
        if (signature.equals("cachePut(Ljava/lang/Long;)V")) {
          return CGLIB$cachePut$1$Proxy;
        }
        break;
      case 1826985398:
        if (signature.equals("equals(Ljava/lang/Object;)Z")) {
          return CGLIB$equals$3$Proxy;
        }
        break;
      case 1913648695:
        if (signature.equals("toString()Ljava/lang/String;")) {
          return CGLIB$toString$4$Proxy;
        }
        break;
      case 1984935277:
        if (signature.equals("hashCode()I")) {
          return CGLIB$hashCode$5$Proxy;
        }
    }

    return null;
  }

  public final int indexOf(Advisor advisor) {
    try {
      Dispatcher dispatcher = this.CGLIB$CALLBACK_4;
      if (this.CGLIB$CALLBACK_4 == null) {
        CGLIB$BIND_CALLBACKS(this);
        dispatcher = this.CGLIB$CALLBACK_4;
      }

      return ((Advised) dispatcher.loadObject()).indexOf(advisor);
    } catch (Error | RuntimeException e) {
      throw e;
    } catch (Throwable e) {
      throw new UndeclaredThrowableException(e);
    }
  }

  public final int indexOf(Advice advisor) {
    try {
      Dispatcher dispatcher = this.CGLIB$CALLBACK_4;
      if (this.CGLIB$CALLBACK_4 == null) {
        CGLIB$BIND_CALLBACKS(this);
        dispatcher = this.CGLIB$CALLBACK_4;
      }

      return ((Advised) dispatcher.loadObject()).indexOf(advisor);
    } catch (Error | RuntimeException e) {
      throw e;
    } catch (Throwable e) {
      throw new UndeclaredThrowableException(e);
    }
  }

  public final boolean isFrozen() {
    try {
      Dispatcher var10000 = this.CGLIB$CALLBACK_4;
      if (this.CGLIB$CALLBACK_4 == null) {
        CGLIB$BIND_CALLBACKS(this);
        var10000 = this.CGLIB$CALLBACK_4;
      }

      return ((Advised) var10000.loadObject()).isFrozen();
    } catch (Error | RuntimeException e) {
      throw e;
    } catch (Throwable e) {
      throw new UndeclaredThrowableException(e);
    }
  }

  public final void setTargetSource(TargetSource var1) {
    try {
      Dispatcher var10000 = this.CGLIB$CALLBACK_4;
      if (this.CGLIB$CALLBACK_4 == null) {
        CGLIB$BIND_CALLBACKS(this);
        var10000 = this.CGLIB$CALLBACK_4;
      }

      ((Advised) var10000.loadObject()).setTargetSource(var1);
    } catch (Error | RuntimeException e) {
      throw e;
    } catch (Throwable e) {
      throw new UndeclaredThrowableException(e);
    }
  }

  public final TargetSource getTargetSource() {
    try {
      Dispatcher var10000 = this.CGLIB$CALLBACK_4;
      if (this.CGLIB$CALLBACK_4 == null) {
        CGLIB$BIND_CALLBACKS(this);
        var10000 = this.CGLIB$CALLBACK_4;
      }

      return ((Advised) var10000.loadObject()).getTargetSource();
    } catch (Error | RuntimeException e) {
      throw e;
    } catch (Throwable e) {
      throw new UndeclaredThrowableException(e);
    }
  }

  public final boolean isProxyTargetClass() {
    try {
      Dispatcher var10000 = this.CGLIB$CALLBACK_4;
      if (this.CGLIB$CALLBACK_4 == null) {
        CGLIB$BIND_CALLBACKS(this);
        var10000 = this.CGLIB$CALLBACK_4;
      }

      return ((Advised) var10000.loadObject()).isProxyTargetClass();
    } catch (Error | RuntimeException e) {
      throw e;
    } catch (Throwable e) {
      throw new UndeclaredThrowableException(e);
    }
  }

  public final void addAdvisor(Advisor advisor) throws AopConfigException {
    try {
      Dispatcher dispatcher = this.CGLIB$CALLBACK_4;
      if (this.CGLIB$CALLBACK_4 == null) {
        CGLIB$BIND_CALLBACKS(this);
        dispatcher = this.CGLIB$CALLBACK_4;
      }

      ((Advised) dispatcher.loadObject()).addAdvisor(advisor);
    } catch (Error | /* AopConfigException | */ RuntimeException e) {
      throw e;
    } catch (Throwable e) {
      throw new UndeclaredThrowableException(e);
    }
  }

  public final void addAdvisor(int index, Advisor advisor) throws AopConfigException {
    try {
      Dispatcher var10000 = this.CGLIB$CALLBACK_4;
      if (this.CGLIB$CALLBACK_4 == null) {
        CGLIB$BIND_CALLBACKS(this);
        var10000 = this.CGLIB$CALLBACK_4;
      }

      ((Advised) var10000.loadObject()).addAdvisor(index, advisor);
    } catch (Error | /* AopConfigException | */ RuntimeException var3) {
      throw var3;
    } catch (Throwable var4) {
      throw new UndeclaredThrowableException(var4);
    }
  }

  public final void setPreFiltered(boolean var1) {
    try {
      Dispatcher var10000 = this.CGLIB$CALLBACK_4;
      if (this.CGLIB$CALLBACK_4 == null) {
        CGLIB$BIND_CALLBACKS(this);
        var10000 = this.CGLIB$CALLBACK_4;
      }

      ((Advised) var10000.loadObject()).setPreFiltered(var1);
    } catch (Error | RuntimeException var2) {
      throw var2;
    } catch (Throwable var3) {
      throw new UndeclaredThrowableException(var3);
    }
  }

  public final void setExposeProxy(boolean var1) {
    try {
      Dispatcher var10000 = this.CGLIB$CALLBACK_4;
      if (this.CGLIB$CALLBACK_4 == null) {
        CGLIB$BIND_CALLBACKS(this);
        var10000 = this.CGLIB$CALLBACK_4;
      }

      ((Advised) var10000.loadObject()).setExposeProxy(var1);
    } catch (Error | RuntimeException var2) {
      throw var2;
    } catch (Throwable var3) {
      throw new UndeclaredThrowableException(var3);
    }
  }

  public final boolean isExposeProxy() {
    try {
      Dispatcher var10000 = this.CGLIB$CALLBACK_4;
      if (this.CGLIB$CALLBACK_4 == null) {
        CGLIB$BIND_CALLBACKS(this);
        var10000 = this.CGLIB$CALLBACK_4;
      }

      return ((Advised) var10000.loadObject()).isExposeProxy();
    } catch (Error | RuntimeException var1) {
      throw var1;
    } catch (Throwable var2) {
      throw new UndeclaredThrowableException(var2);
    }
  }

  public final void addAdvice(Advice var1) throws AopConfigException {
    try {
      Dispatcher var10000 = this.CGLIB$CALLBACK_4;
      if (this.CGLIB$CALLBACK_4 == null) {
        CGLIB$BIND_CALLBACKS(this);
        var10000 = this.CGLIB$CALLBACK_4;
      }

      ((Advised) var10000.loadObject()).addAdvice(var1);
    } catch (Error | /* AopConfigException | */ RuntimeException var2) {
      throw var2;
    } catch (Throwable var3) {
      throw new UndeclaredThrowableException(var3);
    }
  }

  public final void addAdvice(int var1, Advice var2) throws AopConfigException {
    try {
      Dispatcher var10000 = this.CGLIB$CALLBACK_4;
      if (this.CGLIB$CALLBACK_4 == null) {
        CGLIB$BIND_CALLBACKS(this);
        var10000 = this.CGLIB$CALLBACK_4;
      }

      ((Advised) var10000.loadObject()).addAdvice(var1, var2);
    } catch (Error | /* AopConfigException | */ RuntimeException var3) {
      throw var3;
    } catch (Throwable var4) {
      throw new UndeclaredThrowableException(var4);
    }
  }

  public final boolean isPreFiltered() {
    try {
      Dispatcher var10000 = this.CGLIB$CALLBACK_4;
      if (this.CGLIB$CALLBACK_4 == null) {
        CGLIB$BIND_CALLBACKS(this);
        var10000 = this.CGLIB$CALLBACK_4;
      }

      return ((Advised) var10000.loadObject()).isPreFiltered();
    } catch (Error | RuntimeException var1) {
      throw var1;
    } catch (Throwable var2) {
      throw new UndeclaredThrowableException(var2);
    }
  }

  public final Class[] getProxiedInterfaces() {
    try {
      Dispatcher var10000 = this.CGLIB$CALLBACK_4;
      if (this.CGLIB$CALLBACK_4 == null) {
        CGLIB$BIND_CALLBACKS(this);
        var10000 = this.CGLIB$CALLBACK_4;
      }

      return ((Advised) var10000.loadObject()).getProxiedInterfaces();
    } catch (Error | RuntimeException var1) {
      throw var1;
    } catch (Throwable var2) {
      throw new UndeclaredThrowableException(var2);
    }
  }

  public final boolean isInterfaceProxied(Class var1) {
    try {
      Dispatcher var10000 = this.CGLIB$CALLBACK_4;
      if (this.CGLIB$CALLBACK_4 == null) {
        CGLIB$BIND_CALLBACKS(this);
        var10000 = this.CGLIB$CALLBACK_4;
      }

      return ((Advised) var10000.loadObject()).isInterfaceProxied(var1);
    } catch (Error | RuntimeException var2) {
      throw var2;
    } catch (Throwable var3) {
      throw new UndeclaredThrowableException(var3);
    }
  }

  public final Advisor[] getAdvisors() {
    try {
      Dispatcher var10000 = this.CGLIB$CALLBACK_4;
      if (this.CGLIB$CALLBACK_4 == null) {
        CGLIB$BIND_CALLBACKS(this);
        var10000 = this.CGLIB$CALLBACK_4;
      }

      return ((Advised) var10000.loadObject()).getAdvisors();
    } catch (Error | RuntimeException var1) {
      throw var1;
    } catch (Throwable var2) {
      throw new UndeclaredThrowableException(var2);
    }
  }

  public final boolean removeAdvisor(Advisor advisor) {
    try {
      Dispatcher var10000 = this.CGLIB$CALLBACK_4;
      if (this.CGLIB$CALLBACK_4 == null) {
        CGLIB$BIND_CALLBACKS(this);
        var10000 = this.CGLIB$CALLBACK_4;
      }

      return ((Advised) var10000.loadObject()).removeAdvisor(advisor);
    } catch (Error | RuntimeException var2) {
      throw var2;
    } catch (Throwable var3) {
      throw new UndeclaredThrowableException(var3);
    }
  }

  public final void removeAdvisor(int index) throws AopConfigException {
    try {
      Dispatcher var10000 = this.CGLIB$CALLBACK_4;
      if (this.CGLIB$CALLBACK_4 == null) {
        CGLIB$BIND_CALLBACKS(this);
        var10000 = this.CGLIB$CALLBACK_4;
      }

      ((Advised) var10000.loadObject()).removeAdvisor(index);
    } catch (Error | /* AopConfigException | */ RuntimeException var2) {
      throw var2;
    } catch (Throwable var3) {
      throw new UndeclaredThrowableException(var3);
    }
  }

  public final boolean replaceAdvisor(Advisor existing, Advisor replacement)
          throws AopConfigException {
    try {
      Dispatcher var10000 = this.CGLIB$CALLBACK_4;
      if (this.CGLIB$CALLBACK_4 == null) {
        CGLIB$BIND_CALLBACKS(this);
        var10000 = this.CGLIB$CALLBACK_4;
      }

      return ((Advised) var10000.loadObject()).replaceAdvisor(existing, replacement);
    } catch (Error | /* AopConfigException | */ RuntimeException var3) {
      throw var3;
    } catch (Throwable var4) {
      throw new UndeclaredThrowableException(var4);
    }
  }

  public final boolean removeAdvice(Advice advisor) {
    try {
      Dispatcher var10000 = this.CGLIB$CALLBACK_4;
      if (this.CGLIB$CALLBACK_4 == null) {
        CGLIB$BIND_CALLBACKS(this);
        var10000 = this.CGLIB$CALLBACK_4;
      }

      return ((Advised) var10000.loadObject()).removeAdvice(advisor);
    } catch (Error | RuntimeException var2) {
      throw var2;
    } catch (Throwable var3) {
      throw new UndeclaredThrowableException(var3);
    }
  }

  public final String toProxyConfigString() {
    try {
      Dispatcher var10000 = this.CGLIB$CALLBACK_4;
      if (this.CGLIB$CALLBACK_4 == null) {
        CGLIB$BIND_CALLBACKS(this);
        var10000 = this.CGLIB$CALLBACK_4;
      }

      return ((Advised) var10000.loadObject()).toProxyConfigString();
    } catch (Error | RuntimeException var1) {
      throw var1;
    } catch (Throwable var2) {
      throw new UndeclaredThrowableException(var2);
    }
  }

  public final Class getTargetClass() {
    try {
      Dispatcher var10000 = this.CGLIB$CALLBACK_4;
      if (this.CGLIB$CALLBACK_4 == null) {
        CGLIB$BIND_CALLBACKS(this);
        var10000 = this.CGLIB$CALLBACK_4;
      }

      return ((TargetClassAware) var10000.loadObject()).getTargetClass();
    } catch (Error | RuntimeException var1) {
      throw var1;
    } catch (Throwable var2) {
      throw new UndeclaredThrowableException(var2);
    }
  }

  public AspectJDumpTest$AspectJCacheableService$$EnhancerBySpringCGLIB$$2703a4e5() {
    super();
    try {
      CGLIB$BIND_CALLBACKS(this);
    } catch (Error | RuntimeException var1) {
      throw var1;
    } catch (Throwable var2) {
      throw new UndeclaredThrowableException(var2);
    }
  }

  public static void CGLIB$SET_THREAD_CALLBACKS(Callback[] var0) {
    CGLIB$THREAD_CALLBACKS.set(var0);
  }

  public static void CGLIB$SET_STATIC_CALLBACKS(Callback[] var0) {
    CGLIB$STATIC_CALLBACKS = var0;
  }

  private static final void CGLIB$BIND_CALLBACKS(Object toBind) {
    AspectJDumpTest$AspectJCacheableService$$EnhancerBySpringCGLIB$$2703a4e5 proxyToBind = (AspectJDumpTest$AspectJCacheableService$$EnhancerBySpringCGLIB$$2703a4e5) toBind;
    if (!proxyToBind.CGLIB$BOUND) {
      proxyToBind.CGLIB$BOUND = true;
      Object callbacks = CGLIB$THREAD_CALLBACKS.get();
      if (callbacks == null) {
        callbacks = CGLIB$STATIC_CALLBACKS;
        if (CGLIB$STATIC_CALLBACKS == null) {
          return;
        }
      }

      Callback[] callbacksArray = (Callback[]) callbacks;
      proxyToBind.CGLIB$CALLBACK_6 = (MethodInterceptor) ((Callback[]) callbacks)[6];
      proxyToBind.CGLIB$CALLBACK_5 = (MethodInterceptor) callbacksArray[5];
      proxyToBind.CGLIB$CALLBACK_4 = (Dispatcher) callbacksArray[4];
      proxyToBind.CGLIB$CALLBACK_3 = (Dispatcher) callbacksArray[3];
      proxyToBind.CGLIB$CALLBACK_2 = (NoOp) callbacksArray[2];
      proxyToBind.CGLIB$CALLBACK_1 = (MethodInterceptor) callbacksArray[1];
      proxyToBind.CGLIB$CALLBACK_0 = (MethodInterceptor) callbacksArray[0];
    }

  }

  public Object newInstance(Callback[] callbacks) {
    try {
      CGLIB$SET_THREAD_CALLBACKS(callbacks);
      AspectJDumpTest$AspectJCacheableService$$EnhancerBySpringCGLIB$$2703a4e5 var10000 = new AspectJDumpTest$AspectJCacheableService$$EnhancerBySpringCGLIB$$2703a4e5();
      CGLIB$SET_THREAD_CALLBACKS((Callback[]) null);
      return var10000;
    } catch (Error | RuntimeException e) {
      throw e;
    } catch (Throwable e) {
      throw new UndeclaredThrowableException(e);
    }
  }

  public Object newInstance(Callback callback) {
    try {
      throw new IllegalStateException("More than one callback object required");
    } catch (Error | RuntimeException e) {
      throw e;
    } catch (Throwable e) {
      throw new UndeclaredThrowableException(e);
    }
  }

  public Object newInstance(Class[] var1, Object[] var2, Callback[] var3) {
    try {
      CGLIB$SET_THREAD_CALLBACKS(var3);
      switch (var1.length) {
        case 0:
          AspectJDumpTest$AspectJCacheableService$$EnhancerBySpringCGLIB$$2703a4e5 var10000 = new AspectJDumpTest$AspectJCacheableService$$EnhancerBySpringCGLIB$$2703a4e5();
          // var10000.<init>();
          CGLIB$SET_THREAD_CALLBACKS((Callback[]) null);
          return var10000;
        default:
          throw new IllegalArgumentException("Constructor not found");
      }
    } catch (Error | RuntimeException e) {
      throw e;
    } catch (Throwable e) {
      throw new UndeclaredThrowableException(e);
    }
  }

  public Callback getCallback(int index) {
    try {
      CGLIB$BIND_CALLBACKS(this);
      Object callback;
      switch (index) {
        case 0:
          callback = this.CGLIB$CALLBACK_0;
          break;
        case 1:
          callback = this.CGLIB$CALLBACK_1;
          break;
        case 2:
          callback = this.CGLIB$CALLBACK_2;
          break;
        case 3:
          callback = this.CGLIB$CALLBACK_3;
          break;
        case 4:
          callback = this.CGLIB$CALLBACK_4;
          break;
        case 5:
          callback = this.CGLIB$CALLBACK_5;
          break;
        case 6:
          callback = this.CGLIB$CALLBACK_6;
          break;
        default:
          callback = null;
      }

      return (Callback) callback;
    } catch (Error | RuntimeException e) {
      throw e;
    } catch (Throwable e) {
      throw new UndeclaredThrowableException(e);
    }
  }

  public void setCallback(int var1, Callback var2) {
    try {
      switch (var1) {
        case 0:
          this.CGLIB$CALLBACK_0 = (MethodInterceptor) var2;
          break;
        case 1:
          this.CGLIB$CALLBACK_1 = (MethodInterceptor) var2;
          break;
        case 2:
          this.CGLIB$CALLBACK_2 = (NoOp) var2;
          break;
        case 3:
          this.CGLIB$CALLBACK_3 = (Dispatcher) var2;
          break;
        case 4:
          this.CGLIB$CALLBACK_4 = (Dispatcher) var2;
          break;
        case 5:
          this.CGLIB$CALLBACK_5 = (MethodInterceptor) var2;
          break;
        case 6:
          this.CGLIB$CALLBACK_6 = (MethodInterceptor) var2;
      }

    } catch (Error | RuntimeException var3) {
      throw var3;
    } catch (Throwable var4) {
      throw new UndeclaredThrowableException(var4);
    }
  }

  public Callback[] getCallbacks() {
    try {
      CGLIB$BIND_CALLBACKS(this);
      return new Callback[] { this.CGLIB$CALLBACK_0, this.CGLIB$CALLBACK_1,
          this.CGLIB$CALLBACK_2, this.CGLIB$CALLBACK_3, this.CGLIB$CALLBACK_4,
          this.CGLIB$CALLBACK_5, this.CGLIB$CALLBACK_6 };
    } catch (Error | RuntimeException var1) {
      throw var1;
    } catch (Throwable var2) {
      throw new UndeclaredThrowableException(var2);
    }
  }

  public void setCallbacks(Callback[] var1) {
    try {
      this.CGLIB$CALLBACK_0 = (MethodInterceptor) var1[0];
      this.CGLIB$CALLBACK_1 = (MethodInterceptor) var1[1];
      this.CGLIB$CALLBACK_2 = (NoOp) var1[2];
      this.CGLIB$CALLBACK_3 = (Dispatcher) var1[3];
      this.CGLIB$CALLBACK_4 = (Dispatcher) var1[4];
      this.CGLIB$CALLBACK_5 = (MethodInterceptor) var1[5];
      this.CGLIB$CALLBACK_6 = (MethodInterceptor) var1[6];
    } catch (Error | RuntimeException e) {
      throw e;
    } catch (Throwable e) {
      throw new UndeclaredThrowableException(e);
    }
  }

  static {
    CGLIB$STATICHOOK6();
    CGLIB$STATICHOOK5();
  }

  static void CGLIB$STATICHOOK6() {
    try {
      ;
    } catch (Error | RuntimeException e) {
      throw e;
    } catch (Throwable e) {
      throw new UndeclaredThrowableException(e);
    }
  }
}
