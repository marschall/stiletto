package com.github.marschall.stiletto.tests.hierarchy;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class LeafClassTest {

  @Test
  public void isInterface() {
    assertTrue(LeafClass_.class.isAssignableFrom(IntermediateInterface.class));
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
