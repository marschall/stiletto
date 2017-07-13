package com.github.marschall.stiletto.tests.injection;

import java.util.List;

import com.github.marschall.stiletto.api.generation.AdviseBy;

@AdviseBy(CaptureJoinpointAspect.class)
public class CaptureJoinpoint {

  public void method() {
    // empty
  }

  public void method(String s) {
    // empty
  }

  public <T extends Number> T genericMethod(T number) {
    return number;
  }

  public int primitive(int i) {
    return i;
  }

  public int erasure(List<String> list) {
    return list.size();
  }

}
