package com.github.marschall.stiletto.spring;

import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAttribute;

import com.github.marschall.stiletto.api.advice.Around;
import com.github.marschall.stiletto.api.injection.DeclaredAnnotation;
import com.github.marschall.stiletto.api.injection.Evaluate;
import com.github.marschall.stiletto.api.injection.MethodCall;
import com.github.marschall.stiletto.api.invocation.ActualMethodCallWithoutResult;

/**
 * Reimplementation of {@link org.springframework.transaction.interceptor.TransactionInterceptor}.
 */
public class TransactionalAspect {

  private final PlatformTransactionManager txManager;

  public TransactionalAspect(PlatformTransactionManager txManager) {
    this.txManager = txManager;
  }

  @Around
  public void invoke(@DeclaredAnnotation Transactional transactional,
          @Evaluate("${targetClass.name}.${joinpoint.methodName}") String joinpointIdentification,
          @MethodCall ActualMethodCallWithoutResult call) {

    TransactionDefinition definition = new TransactionalTransactionDefinition(transactional, joinpointIdentification);
    TransactionStatus transaction = this.txManager.getTransaction(definition);
    try {
      call.invoke();
    } catch (Error | RuntimeException e) {
      if (rollbackOn(e, transactional)) {
        this.txManager.rollback(transaction);
      }
      throw e;
    }
    this.txManager.commit(transaction);
  }

  private boolean rollbackOn(Throwable ex, Transactional transactional) {
    // FIXME
    return ex instanceof RuntimeException || ex instanceof Error;
  }

  static final class TransactionalTransactionDefinition implements TransactionDefinition {

    private final Transactional transactional;
    private final String joinpointIdentification;

    TransactionalTransactionDefinition(Transactional transactional, String joinpointIdentification) {
      this.transactional = transactional;
      this.joinpointIdentification = joinpointIdentification;
    }

    @Override
    public int getPropagationBehavior() {
      return this.transactional.propagation().value();
    }

    @Override
    public int getIsolationLevel() {
      return this.transactional.isolation().value();
    }

    @Override
    public int getTimeout() {
      return this.transactional.timeout();
    }

    @Override
    public boolean isReadOnly() {
      return this.transactional.readOnly();
    }

    @Override
    public String getName() {
      return this.joinpointIdentification;
    }

  }

  static final class TransactionalTransactionAttribute implements TransactionAttribute {

    private final Transactional transactional;

    TransactionalTransactionAttribute(Transactional transactional) {
      this.transactional = transactional;
    }

    @Override
    public int getPropagationBehavior() {
      return this.transactional.propagation().value();
    }

    @Override
    public int getIsolationLevel() {
      return this.transactional.isolation().value();
    }

    @Override
    public int getTimeout() {
      return this.transactional.timeout();
    }

    @Override
    public boolean isReadOnly() {
      return this.transactional.readOnly();
    }

    @Override
    public String getName() {
      // TODO Auto-generated method stub
      return null;
    }

    @Override
    public String getQualifier() {
      // TODO Auto-generated method stub
      return null;
    }

    @Override
    public boolean rollbackOn(Throwable ex) {
      // TODO Auto-generated method stub
      return true;
    }

  }

}
