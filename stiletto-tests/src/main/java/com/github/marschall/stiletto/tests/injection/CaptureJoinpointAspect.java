package com.github.marschall.stiletto.tests.injection;

import java.lang.reflect.Method;

import com.github.marschall.stiletto.api.advice.Before;
import com.github.marschall.stiletto.api.injection.Joinpoint;

public class CaptureJoinpointAspect {

  private Method joinpoint;

  @Before
  public void captureJoinpoint(@Joinpoint Method joinpoint) {
    this.joinpoint = joinpoint;
  }

  public Method getJoinpoint() {
    return this.joinpoint;
  }

}
