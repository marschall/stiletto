package com.github.marschall.stiletto.spring;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;

import com.github.marschall.stiletto.spring.configuration.StilettoTransactionalConfiguration2;
import com.github.marschall.stiletto.spring.configuration.TransactionManagerConfiguration;

@Transactional
@ContextConfiguration(classes = {TransactionManagerConfiguration.class, StilettoTransactionalConfiguration2.class})
public class StilettoTransactionalTest2 extends AbstractSpringTest {

  @Autowired
  private SimpleTransactionalInterface simpleService;

  @Test
  public void simpleServiceMethod() {
    assertEquals("ok", this.simpleService.simpleServiceMethod());
  }

}
