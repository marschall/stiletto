package com.github.marschall.stiletto.javasimon;

import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.rules.SpringClassRule;
import org.springframework.test.context.junit4.rules.SpringMethodRule;

import com.github.marschall.stiletto.javasimon.configuration.JavaSimonConfiguration;

@ContextConfiguration(classes = JavaSimonConfiguration.class)
public class SpringJavaSimonTest {

  @ClassRule
  public static final SpringClassRule SPRING_CLASS_RULE = new SpringClassRule();

  @Rule
  public final SpringMethodRule springMethodRule = new SpringMethodRule();

  @Autowired
  private SimpleMonitoredInterface monitoredBean;

  @Test
  public void monitoredOperation() {
    this.monitoredBean.operation();
  }

}
