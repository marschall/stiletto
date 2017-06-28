package com.github.marschall.stiletto.tests.advice;

import com.github.marschall.stiletto.api.advice.Before;

public class BeforeAspect {

  private int invocationCount;

  public BeforeAspect() {
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
