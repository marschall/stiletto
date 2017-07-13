package com.github.marschall.stiletto.tests.injection;

import com.github.marschall.stiletto.api.generation.AdviseBy;

@AdviseBy(CaptureJoinpointAspect.class)
public class CaptureJoinpoint {

  public void method() {
    // empty
  }

  public void method(String s) {
    // empty
  }


}
