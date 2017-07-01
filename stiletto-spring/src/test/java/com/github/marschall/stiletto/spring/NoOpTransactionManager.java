package com.github.marschall.stiletto.spring;

import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.SimpleTransactionStatus;

public class NoOpTransactionManager implements PlatformTransactionManager {

  @Override
  public TransactionStatus getTransaction(TransactionDefinition definition) {
    return new SimpleTransactionStatus();
  }

  @Override
  public void commit(TransactionStatus status) {
    // ignore
  }

  @Override
  public void rollback(TransactionStatus status) throws TransactionException {
    // ignore
  }

}
