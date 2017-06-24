package com.github.marschall.stiletto.tests.simple;

import com.github.marschall.stiletto.api.generation.AdviseBy;
import com.github.marschall.stiletto.tests.BeforeCountingAspect;

@AdviseBy(BeforeCountingAspect.class)
public class SimpleAdvisedService {

  public String simpleMethod() {
    return "ok";
  }

}
