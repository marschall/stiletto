package com.github.marschall.stiletto.spring;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;

import com.github.marschall.stiletto.spring.StilettoTransactionalTest2.StilettoTransactionalConfiguration2;
import com.github.marschall.stiletto.spring.configuration.TransactionManagerConfiguration;

@Transactional
@SpringJUnitConfig({TransactionManagerConfiguration.class, StilettoTransactionalConfiguration2.class})
public class StilettoTransactionalTest2 {

  @Autowired
  private SimpleTransactionalInterface simpleService;

  @Test
  public void simpleServiceMethod() {
    assertEquals("ok", this.simpleService.simpleServiceMethod());
  }

  @Configuration
  static class StilettoTransactionalConfiguration2 {

    @Autowired
    private PlatformTransactionManager txManager;

    @Bean
    public SimpleTransactionalInterface simpleService() {
      SimpleTransactionalService2 targetObject = new SimpleTransactionalService2();
      TransactionalAspect2 aspect = new TransactionalAspect2(this.txManager);
      return new SimpleTransactionalService2_(targetObject, aspect);
    }

  }

}
