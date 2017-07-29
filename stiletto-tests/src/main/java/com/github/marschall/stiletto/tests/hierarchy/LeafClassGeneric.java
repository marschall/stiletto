package com.github.marschall.stiletto.tests.hierarchy;

import com.github.marschall.stiletto.api.generation.AdviseBy;
import com.github.marschall.stiletto.tests.BeforeCountingAspect;

@AdviseBy(BeforeCountingAspect.class)
public class LeafClassGeneric extends IntermediateClassGeneric implements IntermediateInterfaceGeneric<String> {

  @Override
  public String simpleMethod() {
    return "ok";
  }

}
