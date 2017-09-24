package com.github.marschall.stiletto.javasimon.configuration;

import org.javasimon.spring.MonitoredMeasuringPointcut;
import org.javasimon.spring.MonitoringInterceptor;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JavaSimonConfiguration {

  @Bean
  public DefaultPointcutAdvisor monitoringAdvisor() {
      DefaultPointcutAdvisor monitoringAdvisor = new DefaultPointcutAdvisor();
      monitoringAdvisor.setAdvice(new MonitoringInterceptor());
      monitoringAdvisor.setPointcut(new MonitoredMeasuringPointcut());
      return monitoringAdvisor;
  }

}
