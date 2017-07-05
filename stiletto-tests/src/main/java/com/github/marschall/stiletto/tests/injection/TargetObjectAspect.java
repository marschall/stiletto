package com.github.marschall.stiletto.tests.injection;

import com.github.marschall.stiletto.api.advice.Before;
import com.github.marschall.stiletto.api.injection.TargetObject;

public class TargetObjectAspect {

  private Object targetObject;

  @Before
  public void captureTargetObject(@TargetObject Object targetObject) {
    this.targetObject = targetObject;
  }

  public Object geTargetObject() {
    return this.targetObject;
  }

}
