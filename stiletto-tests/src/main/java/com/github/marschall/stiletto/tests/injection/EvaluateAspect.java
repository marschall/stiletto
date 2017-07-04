package com.github.marschall.stiletto.tests.injection;

import com.github.marschall.stiletto.api.advice.Before;
import com.github.marschall.stiletto.api.injection.Evaluate;

public class EvaluateAspect {

  private String value;

  @Before
  public void logEntering(@Evaluate("prefix ${targetClass.fullyQualifiedName}.${joinpoint.methodSignature}") String value) {
    this.value = value;
  }

  public String getValue() {
    return this.value;
  }

}
