package com.github.marschall.stiletto.spring;

import java.util.Objects;

import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.CallbackPreferringPlatformTransactionManager;

import com.github.marschall.stiletto.api.advice.Around;
import com.github.marschall.stiletto.api.advice.WithAnnotationMatching;
import com.github.marschall.stiletto.api.injection.DeclaredAnnotation;
import com.github.marschall.stiletto.api.injection.Evaluate;
import com.github.marschall.stiletto.api.injection.MethodCall;
import com.github.marschall.stiletto.api.invocation.ActualMethodCallWithResult;

/**
 * Reimplementation of {@link org.springframework.transaction.interceptor.TransactionInterceptor}.
 *
 * <h2>Not supported</h2>
 * <ul>
 *  <li>Dynamic lookup of transaction manager using
 *  {@link org.springframework.transaction.annotation.Transactional.transactionManager()}.</li>
 *  <li><a href="https://docs.spring.io/spring/docs/current/spring-framework-reference/htmlsingle/#beans-meta-annotations">
 *  Meta annotations including {@link org.springframework.core.annotation.AliasFor}.</li>
 *  <li>{@link org.springframework.transaction.interceptor.TransactionAspectSupport.currentTransactionStatus()}
 *  is not supported but seems unused..</li>
 * </ul>
 */
public final class TransactionalAspect extends AbstractTransactionalAspect {

  private final PlatformTransactionManager txManager;

  public TransactionalAspect(PlatformTransactionManager txManager) {
    Objects.requireNonNull(txManager, "txManager");
    if (txManager instanceof CallbackPreferringPlatformTransactionManager) {
      throw new IllegalArgumentException("use CallbackPreferringTransactionalAspect for CallbackPreferringPlatformTransactionManager");
    }
    this.txManager = txManager;
  }

  @Around
  @Transactional
  @WithAnnotationMatching(Transactional.class)
  public Object invoke(@DeclaredAnnotation Transactional transactional,
          @Evaluate("${targetClass.fullyQualifiedName}.${joinpoint.methodName}") String joinpointIdentification,
          @MethodCall ActualMethodCallWithResult<?> call) {

    TransactionDefinition definition = newTransactionDefinition(transactional, joinpointIdentification);
    TransactionStatus transaction = this.txManager.getTransaction(definition);

    Object result;
    try {
      result = call.invoke();
    } catch (Error | RuntimeException e) {
      // org.springframework.transaction.interceptor.TransactionAspectSupport.completeTransactionAfterThrowing(TransactionInfo, Throwable)
      if (rollbackOn(e, transactional)) {
        this.txManager.rollback(transaction);
      }
      throw e;
    }
    this.txManager.commit(transaction);
    return result;
  }

}
