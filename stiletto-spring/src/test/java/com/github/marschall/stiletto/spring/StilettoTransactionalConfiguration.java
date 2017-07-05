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
  public SimpleTransactionalInterface simpleService() {
    SimpleTransactionalService targetObject = new SimpleTransactionalService();
    TransactionalAspect aspect = new TransactionalAspect(this.txManager());
    return new SimpleTransactionalService_(targetObject, aspect);
  }

}
