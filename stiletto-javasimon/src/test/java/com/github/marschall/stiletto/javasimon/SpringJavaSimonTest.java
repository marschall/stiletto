package com.github.marschall.stiletto.javasimon;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import com.github.marschall.stiletto.javasimon.configuration.JavaSimonConfiguration;

@SpringJUnitConfig(JavaSimonConfiguration.class)
public class SpringJavaSimonTest {

  @Autowired
  private SimpleMonitoredInterface monitoredBean;

  @Test
  public void monitoredOperation() {
    this.monitoredBean.operation();
  }

}
