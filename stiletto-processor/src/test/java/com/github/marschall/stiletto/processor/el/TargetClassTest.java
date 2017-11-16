package com.github.marschall.stiletto.processor.el;


import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TargetClassTest {

  private TargetClass targetClass;

  @BeforeEach
  public void setUp() {
    this.targetClass = new TargetClass("java.lang.Object");
  }

  @Test
  public void getSimpleName() {
    assertEquals("Object", this.targetClass.getSimpleName());
  }

  @Test
  public void getName() {
    assertEquals("java.lang.Object", this.targetClass.getFullyQualifiedName());
  }

  @Test
  public void defaultPackage() {
    assertEquals("NotAdvised", new TargetClass("NotAdvised").getSimpleName());
  }

}
