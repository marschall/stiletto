package com.github.marschall.stiletto.spring;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
public class SpringTransactionalConfiguration {

  @Bean
  public PlatformTransactionManager txManager() {
    return new NoOpTransactionManager();
  }

  @Bean
  public SimpleTransactionalInterface simpleService() {
    return new SimpleTransactionalService();
  }

}
