package com.github.marschall.stiletto.tests.hierarchy;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class LeafClassTest {

  @Test
  public void isInterface() {
    assertSame(Object.class, LeafClass_.class.getSuperclass());
    assertArrayEquals(new Class[] {IntermediateInterface.class}, LeafClass_.class.getInterfaces());
  }

  @Test
  public void intermediateInterface() {
    assertTrue(IntermediateInterface.class.isAssignableFrom(LeafClass_.class));
  }

  @Test
  public void sameInterfaceTwice() {
    assertTrue(IntermediateInterface.class.isAssignableFrom(LeafClassSameInterface_.class));
  }

  @Test
  public void genericInterface() {
    assertTrue(IntermediateInterfaceGeneric.class.isAssignableFrom(LeafClassGeneric_.class));
  }

}
