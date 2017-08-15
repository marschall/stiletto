package com.github.marschall.stiletto.spring;

import java.util.Objects;

import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.CallbackPreferringPlatformTransactionManager;

import com.github.marschall.stiletto.api.advice.AfterReturning;
import com.github.marschall.stiletto.api.advice.AfterThrowing;
import com.github.marschall.stiletto.api.advice.Before;
import com.github.marschall.stiletto.api.injection.BeforeValue;
import com.github.marschall.stiletto.api.injection.DeclaredAnnotation;
import com.github.marschall.stiletto.api.injection.Evaluate;
import com.github.marschall.stiletto.api.injection.Thrown;
import com.github.marschall.stiletto.api.pointcut.Matching;

public class TransactionalAspect2 extends AbstractTransactionalAspect {

  private final PlatformTransactionManager txManager;

  public TransactionalAspect2(PlatformTransactionManager txManager) {
    Objects.requireNonNull(txManager, "txManager");
    if (txManager instanceof CallbackPreferringPlatformTransactionManager) {
      throw new IllegalArgumentException("use CallbackPreferringTransactionalAspect for CallbackPreferringPlatformTransactionManager");
    }
    this.txManager = txManager;
  }

  @Before
  @Transactional
  @Matching(Transactional.class)
  public TransactionStatus beginTransaction(@DeclaredAnnotation Transactional transactional,
          @Evaluate("${targetClass.fullyQualifiedName}.${joinpoint.methodName}") String joinpointIdentification) {

    TransactionDefinition definition = this.newTransactionDefinition(transactional, joinpointIdentification);
    return this.txManager.getTransaction(definition);
  }

  @AfterReturning
  @Transactional
  @Matching(Transactional.class)
  public void commitTransaction(@BeforeValue TransactionStatus transaction) {
    this.txManager.commit(transaction);
  }

  @AfterThrowing(RuntimeException.class)
  @Transactional
  @Matching(Transactional.class)
  public void onException(@BeforeValue TransactionStatus transaction,
          @DeclaredAnnotation Transactional transactional,
          @Thrown RuntimeException e) {
    this.commitOrRollback(transaction, transactional, e);
    throw e;
  }

  @AfterThrowing(Error.class)
  @Transactional
  @Matching(Transactional.class)
  public void onError(@BeforeValue TransactionStatus transaction,
          @DeclaredAnnotation Transactional transactional,
          @Thrown Error e) {
    this.commitOrRollback(transaction, transactional, e);
    throw e;
  }

  private void commitOrRollback(TransactionStatus transaction, Transactional transactional, Throwable e) {
    if (rollbackOn(e, transactional)) {
      this.txManager.rollback(transaction);
    } else {
      this.txManager.commit(transaction);
    }
  }

}
