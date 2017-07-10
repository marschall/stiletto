package com.github.marschall.stiletto.tests.injection;

import com.github.marschall.stiletto.api.generation.AdviseBy;

@AdviseBy(CaptureArgumentsAspect.class)
public class CaptureArguments {

  public String noArguments() {
    return "noArguments";
  }

  public String oneArgument(String s) {
    return "oneArgument";
  }

  public String onePrimitiveArgument(int i) {
    return "onePrimitiveArgument";
  }

  public String arrayArgument(Object[] argument) {
    return "arrayArgument";
  }

}
