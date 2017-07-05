package com.github.marschall.stiletto.spring;

import org.springframework.transaction.annotation.Transactional;

import com.github.marschall.stiletto.api.generation.AdviseBy;

@Transactional
@AdviseBy(TransactionalAspect.class)
public class SimpleTransactionalService implements SimpleTransactionalInterface {

  @Override
  public String simpleServiceMethod() {
    return "ok";
  }

}
