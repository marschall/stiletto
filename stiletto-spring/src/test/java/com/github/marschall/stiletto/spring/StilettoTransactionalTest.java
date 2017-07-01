package com.github.marschall.stiletto.spring;

import static org.junit.Assert.assertEquals;

import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.rules.SpringClassRule;
import org.springframework.test.context.junit4.rules.SpringMethodRule;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@ContextConfiguration(classes = StilettoTransactionalConfiguration.class)
public class StilettoTransactionalTest {

  @ClassRule
  public static final SpringClassRule SPRING_CLASS_RULE = new SpringClassRule();

  @Rule
  public final SpringMethodRule springMethodRule = new SpringMethodRule();

  @Autowired
  private SimpleServiceInterface simpleService;

  @Test
  public void simpleServiceMethod() {
    assertEquals("ok", this.simpleService.simpleServiceMethod());
  }

}
