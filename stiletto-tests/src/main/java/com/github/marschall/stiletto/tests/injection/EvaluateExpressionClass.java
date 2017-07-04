package com.github.marschall.stiletto.tests.injection;

import com.github.marschall.stiletto.api.generation.AdviseBy;

@AdviseBy(EvaluateAspect.class)
public class EvaluateExpressionClass {

  public void noArguments() {
    // ignore
  }

  public String oneArgument(String s) {
    return s;
  }

  public int twoArguments(int i, int j) {
    return i + j;
  }

}
