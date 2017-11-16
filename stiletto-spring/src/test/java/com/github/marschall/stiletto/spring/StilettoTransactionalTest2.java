package com.github.marschall.stiletto.spring;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.transaction.annotation.Transactional;

import com.github.marschall.stiletto.spring.configuration.StilettoTransactionalConfiguration2;
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

}
