package com.github.marschall.stiletto.jperf.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import com.github.marschall.stiletto.jperf.NoopTimingAspect;

@Configuration
@EnableAspectJAutoProxy
public class JPerfConfiguration {

  @Bean
  public NoopTimingAspect timingAspect() {
    return new NoopTimingAspect();
  }

}
