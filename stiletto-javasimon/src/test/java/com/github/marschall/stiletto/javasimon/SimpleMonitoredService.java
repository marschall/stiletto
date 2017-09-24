package com.github.marschall.stiletto.javasimon;

import org.javasimon.aop.Monitored;

//@AdviseBy(JavaSimonAspect.class)
public class SimpleMonitoredService implements SimpleMonitoredInterface {

  @Override
  @Monitored
  public void operation() {
    // nothing
  }

}
