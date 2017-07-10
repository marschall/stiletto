package com.github.marschall.stiletto.tests.injection;

import static org.junit.Assert.assertSame;

import org.junit.Before;
import org.junit.Test;

public class TargetObjectTest {

  private TargetObjectAspect aspect;

  @Before
  public void setUp() {
    this.aspect = new TargetObjectAspect();
  }

  @Test
  public void simpleMethod() {
    TargetObjectInterface targetObject = () -> {};

    TargetObjectInterface proxy = new TargetObjectInterface_(targetObject, this.aspect);

    proxy.simpleMethod();
    assertSame(targetObject, this.aspect.geTargetObject());
  }

}
