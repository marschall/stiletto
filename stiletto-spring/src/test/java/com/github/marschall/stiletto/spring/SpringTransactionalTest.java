package com.github.marschall.stiletto.spring;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;

import com.github.marschall.stiletto.spring.SpringTransactionalTest.SpringTransactionalConfiguration;
import com.github.marschall.stiletto.spring.configuration.TransactionManagerConfiguration;

@Transactional
@SpringJUnitConfig({TransactionManagerConfiguration.class, SpringTransactionalConfiguration.class})
public class SpringTransactionalTest {

  @Autowired
  private SimpleTransactionalInterface simpleService;

  @Test
  public void simpleServiceMethod() {
    assertEquals("ok", this.simpleService.simpleServiceMethod());
  }


  @Configuration
  @EnableTransactionManagement
  static class SpringTransactionalConfiguration {

    @Bean
    public SimpleTransactionalInterface simpleService() {
      return new SimpleTransactionalService();
    }

  }

}
