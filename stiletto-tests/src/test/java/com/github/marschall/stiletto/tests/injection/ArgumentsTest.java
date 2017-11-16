package com.github.marschall.stiletto.tests.injection;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ArgumentsTest {

  private CaptureArgumentsAspect aspect;
  private CaptureArguments proxy;

  @BeforeEach
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
