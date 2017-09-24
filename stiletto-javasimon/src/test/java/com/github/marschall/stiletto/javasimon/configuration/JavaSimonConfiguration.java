package com.github.marschall.stiletto.javasimon.configuration;

import org.javasimon.spring.MonitoredMeasuringPointcut;
import org.javasimon.spring.MonitoringInterceptor;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import com.github.marschall.stiletto.javasimon.SimpleMonitoredInterface;
import com.github.marschall.stiletto.javasimon.SimpleMonitoredService;

@Configuration
@EnableAspectJAutoProxy
public class JavaSimonConfiguration {

  @Bean
  public DefaultPointcutAdvisor monitoringAdvisor() {
    DefaultPointcutAdvisor monitoringAdvisor = new DefaultPointcutAdvisor();
    monitoringAdvisor.setAdvice(new MonitoringInterceptor());
    monitoringAdvisor.setPointcut(new MonitoredMeasuringPointcut());
    return monitoringAdvisor;
  }

  @Bean
  public SimpleMonitoredInterface monitoredBean() {
    return new SimpleMonitoredService();
  }

}
