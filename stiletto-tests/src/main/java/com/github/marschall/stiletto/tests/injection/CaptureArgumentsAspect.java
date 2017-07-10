package com.github.marschall.stiletto.tests.injection;

import com.github.marschall.stiletto.api.advice.Before;
import com.github.marschall.stiletto.api.injection.Arguments;

public class CaptureArgumentsAspect {

  private Object[] arguments;

  @Before
  public void captureArguments(@Arguments Object[] arguments) {
    this.arguments = arguments;
  }

  public Object[] getArguments() {
    return this.arguments;
  }

}
