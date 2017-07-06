package com.github.marschall.stiletto.spring;

import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;

import com.github.marschall.stiletto.api.advice.Around;
import com.github.marschall.stiletto.api.advice.WithAnnotationMatching;
import com.github.marschall.stiletto.api.injection.DeclaredAnnotation;
import com.github.marschall.stiletto.api.injection.Evaluate;
import com.github.marschall.stiletto.api.injection.MethodCall;
import com.github.marschall.stiletto.api.invocation.ActualMethodCallWithoutResult;

/**
 * Reimplementation of {@link org.springframework.transaction.interceptor.TransactionInterceptor}.
 *
 * <h2>Not supported</h2>
 * <ul>
 *  <li>Dynamic lookup of transaction manager using
 *  {@link org.springframework.transaction.annotation.Transactional.transactionManager()}.</li>
 *  <li><a href="https://docs.spring.io/spring/docs/current/spring-framework-reference/htmlsingle/#beans-meta-annotations">
 *  Meta annotations including {@link org.springframework.core.annotation.AliasFor}.</li>
 * </ul>
 */
public class TransactionalAspect {

  private final PlatformTransactionManager txManager;

  public TransactionalAspect(PlatformTransactionManager txManager) {
    this.txManager = txManager;
  }

  @Around
  @Transactional
  @WithAnnotationMatching(Transactional.class)
  public void invoke(@DeclaredAnnotation Transactional transactional,
          @Evaluate("${targetClass.fullyQualifiedName}.${joinpoint.methodName}") String joinpointIdentification,
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

}
