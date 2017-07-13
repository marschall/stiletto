package com.github.marschall.stiletto.tests.injection;

import static org.junit.Assert.*;

import java.lang.reflect.Method;

import org.junit.Before;
import org.junit.Test;

public class JoinpointTest {

  private CaptureJoinpointAspect aspect;
  private CaptureJoinpoint proxy;

  @Before
  public void setUp() {
    this.aspect = new CaptureJoinpointAspect();
    CaptureJoinpoint targetObject = new CaptureJoinpoint();
    this.proxy = new CaptureJoinpoint_(targetObject, this.aspect);
  }

  @Test
  public void noArguments() {
    this.proxy.method();

    Method joinpoint = this.aspect.getJoinpoint();
    assertNotNull(joinpoint);
    assertEquals("method", joinpoint.getName());
    assertEquals(CaptureJoinpoint.class, joinpoint.getDeclaringClass());
    assertArrayEquals(new Class[0], joinpoint.getParameterTypes());
  }

  @Test
  public void oneArgument() {
    this.proxy.method("s");

    Method joinpoint = this.aspect.getJoinpoint();
    assertNotNull(joinpoint);
    assertEquals("method", joinpoint.getName());
    assertEquals(CaptureJoinpoint.class, joinpoint.getDeclaringClass());
    assertArrayEquals(new Class[] {String.class}, joinpoint.getParameterTypes());
  }

}
