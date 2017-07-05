package com.github.marschall.stiletto.spring.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.github.marschall.stiletto.spring.NoOpTransactionManager;
import com.github.marschall.stiletto.spring.SimpleTransactionalInterface;
import com.github.marschall.stiletto.spring.SimpleTransactionalService;

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
