package com.github.marschall.stiletto.tests.injection;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;

public class ArgumentsTest {

  private CaptureArgumentsAspect aspect;
  private CaptureArguments proxy;

  @Before
  public void setUp() {
    this.aspect = new CaptureArgumentsAspect();
    CaptureArguments targetObject = new CaptureArguments();
    this.proxy = new CaptureArguments_(targetObject, this.aspect);
  }

  @Test
  public void noArguments() {
    assertEquals("noArguments", this.proxy.noArguments());
    assertNull(this.aspect.getArguments());
  }

  @Test
  public void oneArgument() {
    assertEquals("oneArgument", this.proxy.oneArgument("one"));
    assertArrayEquals(new Object[] {"one"}, this.aspect.getArguments());
  }

  @Test
  public void twoArguments() {
    assertEquals("twoArguments", this.proxy.twoArguments("one", "two"));
    assertArrayEquals(new Object[] {"one", "two"}, this.aspect.getArguments());
  }

  @Test
  public void onePrimitiveArgument() {
    assertEquals("onePrimitiveArgument", this.proxy.onePrimitiveArgument(23));
    assertArrayEquals(new Object[] {23}, this.aspect.getArguments());
  }

  @Test
  public void arrayArgument() {
    assertEquals("arrayArgument", this.proxy.arrayArgument(new String[] {"1", "2"}));
    assertArrayEquals(new Object[][] {new String[] {"1", "2"}}, this.aspect.getArguments());
  }

}
