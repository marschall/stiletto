package com.github.marschall.stiletto.jperf;

import com.github.marschall.stiletto.api.generation.AdviseBy;

import net.jperf.aop.Profiled;

@AdviseBy(NoopTimingAspect.class)
public class SimpleProfiledService implements SimpleProfiledInterface {

  @Override
  @Profiled
  public void operation() {
    // nothing
  }

}
