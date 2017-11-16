package com.github.marschall.stiletto.javasimon;

import org.javasimon.aop.Monitored;

import com.github.marschall.stiletto.api.generation.AdviseBy;

@AdviseBy(JavaSimonAspect.class)
public class SimpleMonitoredService implements SimpleMonitoredInterface {

  @Override
  @Monitored
  public void operation() {
    // nothing
  }

}
