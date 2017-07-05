package com.github.marschall.stiletto.spring.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import com.github.marschall.stiletto.spring.NoOpTransactionManager;
import com.github.marschall.stiletto.spring.SimpleTransactionalInterface;
import com.github.marschall.stiletto.spring.SimpleTransactionalService;
import com.github.marschall.stiletto.spring.SimpleTransactionalService_;
import com.github.marschall.stiletto.spring.TransactionalAspect;

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
