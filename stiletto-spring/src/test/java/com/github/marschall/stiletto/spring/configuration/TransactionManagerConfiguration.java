package com.github.marschall.stiletto.spring.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import com.github.marschall.stiletto.spring.NoOpTransactionManager;

@Configuration
public class TransactionManagerConfiguration {

  @Bean
  public PlatformTransactionManager txManager() {
    return new NoOpTransactionManager();
  }

}
