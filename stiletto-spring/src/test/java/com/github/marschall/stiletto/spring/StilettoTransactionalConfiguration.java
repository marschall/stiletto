package com.github.marschall.stiletto.spring;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
// no @EnableTransactionManagement
public class StilettoTransactionalConfiguration {

  @Bean
  public PlatformTransactionManager txManager() {
    return new NoOpTransactionManager();
  }

  @Bean
  public SimpleServiceInterface simpleService() {
    SimpleServie targetObject = new SimpleServie();
    TransactionalAspect aspect = new TransactionalAspect(this.txManager());
    return new SimpleServie_(targetObject, aspect);
  }

}
