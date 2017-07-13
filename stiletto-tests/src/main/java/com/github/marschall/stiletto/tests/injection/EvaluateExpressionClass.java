package com.github.marschall.stiletto.tests.injection;

import java.util.List;

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

  public <T extends Number> T genericMethod(T number) {
    return number;
  }

  public int erasure(List<String> list) {
    return list.size();
  }

  public int array(String[] array) {
    return array.length;
  }

}
