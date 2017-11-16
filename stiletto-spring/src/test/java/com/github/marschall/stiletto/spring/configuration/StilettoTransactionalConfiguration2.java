package com.github.marschall.stiletto.spring.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import com.github.marschall.stiletto.spring.SimpleTransactionalInterface;
import com.github.marschall.stiletto.spring.SimpleTransactionalService;
import com.github.marschall.stiletto.spring.SimpleTransactionalService_;
import com.github.marschall.stiletto.spring.TransactionalAspect;

@Configuration
public class StilettoTransactionalConfiguration2 {

  @Autowired
  private PlatformTransactionManager txManager;

  @Bean
  public SimpleTransactionalInterface simpleService() {
    SimpleTransactionalService targetObject = new SimpleTransactionalService();
    TransactionalAspect aspect = new TransactionalAspect(this.txManager);
    return new SimpleTransactionalService_(targetObject, aspect);
  }

}
