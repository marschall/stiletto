package com.github.marschall.stiletto.tests.injection;

import static org.junit.jupiter.api.Assertions.assertSame;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TargetObjectTest {

  private TargetObjectAspect aspect;

  @BeforeEach
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
