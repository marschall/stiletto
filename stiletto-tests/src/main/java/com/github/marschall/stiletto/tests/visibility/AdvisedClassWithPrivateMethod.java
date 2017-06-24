package com.github.marschall.stiletto.tests.visibility;

import com.github.marschall.stiletto.api.generation.AdviseBy;
import com.github.marschall.stiletto.tests.BeforeCountingAspect;

@AdviseBy(BeforeCountingAspect.class)
public class AdvisedClassWithPrivateMethod {

  public String simpleMethod() {
    return this.privateMethod();
  }

  private String privateMethod() {
    return "ok";
  }

  public static String staticMethod() {
    return "static";
  }

}
