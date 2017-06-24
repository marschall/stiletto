package com.github.marschall.stiletto.tests;

import com.github.marschall.stiletto.api.advice.Before;

public class BeforeCountingAspect {

  private int invocationCount;

  public BeforeCountingAspect() {
    this.invocationCount = 0;
  }

  @Before
  public void before() {
    this.invocationCount += 1;
  }

  public int getInvocationCount() {
    return invocationCount;
  }

}
