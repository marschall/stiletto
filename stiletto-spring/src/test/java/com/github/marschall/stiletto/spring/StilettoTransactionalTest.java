package com.github.marschall.stiletto.spring;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;

import com.github.marschall.stiletto.spring.StilettoTransactionalTest.StilettoTransactionalConfiguration;
import com.github.marschall.stiletto.spring.configuration.TransactionManagerConfiguration;

@Transactional
@SpringJUnitConfig({TransactionManagerConfiguration.class, StilettoTransactionalConfiguration.class})
public class StilettoTransactionalTest {

  @Autowired
  private SimpleTransactionalInterface simpleService;

  @Test
  public void simpleServiceMethod() {
    assertEquals("ok", this.simpleService.simpleServiceMethod());
  }

  @Configuration
  static class StilettoTransactionalConfiguration {

    @Autowired
    private PlatformTransactionManager txManager;

    @Bean
    public SimpleTransactionalInterface simpleService() {
      SimpleTransactionalService targetObject = new SimpleTransactionalService();
      TransactionalAspect aspect = new TransactionalAspect(this.txManager);
      return new SimpleTransactionalService_(targetObject, aspect);
    }

  }

}
