package com.github.marschall.stiletto.tests.injection;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;

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

  @Test
  public void genericMethod() {
    this.proxy.genericMethod(1L);

    Method joinpoint = this.aspect.getJoinpoint();
    assertNotNull(joinpoint);
    assertEquals("genericMethod", joinpoint.getName());
    assertEquals(CaptureJoinpoint.class, joinpoint.getDeclaringClass());
    assertArrayEquals(new Class[] {Number.class}, joinpoint.getParameterTypes());
  }

  @Test
  public void primitive() {
    this.proxy.primitive(1);

    Method joinpoint = this.aspect.getJoinpoint();
    assertNotNull(joinpoint);
    assertEquals("primitive", joinpoint.getName());
    assertEquals(CaptureJoinpoint.class, joinpoint.getDeclaringClass());
    assertArrayEquals(new Class[] {int.class}, joinpoint.getParameterTypes());
  }

  @Test
  public void erasure() {
    this.proxy.erasure(Collections.singletonList("1"));

    Method joinpoint = this.aspect.getJoinpoint();
    assertNotNull(joinpoint);
    assertEquals("erasure", joinpoint.getName());
    assertEquals(CaptureJoinpoint.class, joinpoint.getDeclaringClass());
    assertArrayEquals(new Class[] {List.class}, joinpoint.getParameterTypes());
  }

  @Test
  public void array() {
    this.proxy.array(new String[] {"1"});

    Method joinpoint = this.aspect.getJoinpoint();
    assertNotNull(joinpoint);
    assertEquals("array", joinpoint.getName());
    assertEquals(CaptureJoinpoint.class, joinpoint.getDeclaringClass());
    assertArrayEquals(new Class[] {String[].class}, joinpoint.getParameterTypes());
  }

}
