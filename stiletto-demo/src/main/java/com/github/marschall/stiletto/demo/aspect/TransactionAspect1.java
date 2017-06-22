package com.github.marschall.stiletto.demo.aspect;

import org.springframework.transaction.annotation.Transactional;

public class TransactionAspect1 {

  @Transactional
  public void inTransaction() {

  }

}
