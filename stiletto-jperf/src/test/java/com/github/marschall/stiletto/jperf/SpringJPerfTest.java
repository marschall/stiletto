package com.github.marschall.stiletto.jperf;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import com.github.marschall.stiletto.jperf.SpringJPerfTest.SpringJPerfConfiguration;
import com.github.marschall.stiletto.jperf.configuration.JPerfConfiguration;

@SpringJUnitConfig({JPerfConfiguration.class, SpringJPerfConfiguration.class})
public class SpringJPerfTest {

  @Autowired
  private SimpleProfiledInterface simpleProfiledInterface;

  @Test
  public void operation() {
    this.simpleProfiledInterface.operation();
  }

  @Configuration
  static class SpringJPerfConfiguration {

    @Bean
    public SimpleProfiledInterface simpleProfiledInterface() {
      return new SimpleProfiledService();
    }

  }

}
