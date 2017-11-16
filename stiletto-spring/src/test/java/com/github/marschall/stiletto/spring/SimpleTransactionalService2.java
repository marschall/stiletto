package com.github.marschall.stiletto.spring;

import org.springframework.transaction.annotation.Transactional;

import com.github.marschall.stiletto.api.generation.AdviseBy;

@Transactional
@AdviseBy(TransactionalAspect2.class)
public class SimpleTransactionalService2 implements SimpleTransactionalInterface {

  @Override
  public String simpleServiceMethod() {
    return "ok";
  }

}
