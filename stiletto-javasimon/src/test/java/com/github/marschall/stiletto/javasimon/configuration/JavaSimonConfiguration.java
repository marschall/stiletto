package com.github.marschall.stiletto.javasimon.configuration;

import org.javasimon.DisabledManager;
import org.javasimon.Manager;
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
  public Manager simonManager() {
    return new DisabledManager();
  }

  @Bean
  public DefaultPointcutAdvisor monitoringAdvisor() {
    DefaultPointcutAdvisor monitoringAdvisor = new DefaultPointcutAdvisor();
    monitoringAdvisor.setAdvice(new MonitoringInterceptor(this.simonManager()));
    monitoringAdvisor.setPointcut(new MonitoredMeasuringPointcut());
    return monitoringAdvisor;
  }

  @Bean
  public SimpleMonitoredInterface monitoredBean() {
    return new SimpleMonitoredService();
  }

}
