package com.github.marschall.stiletto.spring;

import java.util.Objects;

import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.CallbackPreferringPlatformTransactionManager;
import org.springframework.transaction.support.TransactionCallback;

import com.github.marschall.stiletto.api.advice.Around;
import com.github.marschall.stiletto.api.injection.DeclaredAnnotation;
import com.github.marschall.stiletto.api.injection.Evaluate;
import com.github.marschall.stiletto.api.injection.MethodCall;
import com.github.marschall.stiletto.api.invocation.ActualMethodCall;
import com.github.marschall.stiletto.api.pointcut.Matching;

/**
 * Reimplementation of {@link org.springframework.transaction.interceptor.TransactionInterceptor}
 * for a {@link CallbackPreferringPlatformTransactionManager}.
 */
public final class CallbackPreferringTransactionalAspect extends AbstractTransactionalAspect {

  private final CallbackPreferringPlatformTransactionManager txManager;

  public CallbackPreferringTransactionalAspect(CallbackPreferringPlatformTransactionManager txManager) {
    Objects.requireNonNull(txManager, "txManager");
    this.txManager = txManager;
  }

  @Around
  @Transactional
  @Matching(Transactional.class)
  public Object invoke(@DeclaredAnnotation Transactional transactional,
          @Evaluate("${targetClass.fullyQualifiedName}.${joinpoint.methodName}") String joinpointIdentification,
          @MethodCall ActualMethodCall<?> call) {

    TransactionDefinition definition = newTransactionDefinition(transactional, joinpointIdentification);

    Object result = this.txManager.execute(definition, new TransactionCallback<Object>() {

      @Override
      public Object doInTransaction(TransactionStatus status) {
        try {
          return call.invoke();
        } catch (RuntimeException e) {
          if (rollbackOn(e, transactional)) {
            // any exception will lead to a rollback.
            throw e;
          } else {
            // a normal return value will lead to a commit
            return new RuntimeExceptionHolder(e);
          }
        } catch (Error e) {
          if (rollbackOn(e, transactional)) {
            // any exception will lead to a rollback.
            throw e;
          } else {
            // a normal return value will lead to a commit
            return new ErrorHolder(e);
          }
        }
      }
    });

    if (result instanceof RuntimeExceptionHolder) {
      throw ((RuntimeExceptionHolder) result).getRuntimeException();
    }

    if (result instanceof ErrorHolder) {
      throw ((ErrorHolder) result).getError();
    }

    return result;
  }


  static final class RuntimeExceptionHolder {

    private final RuntimeException throwable;

    RuntimeExceptionHolder(RuntimeException runtimeException) {
      this.throwable = runtimeException;
    }

    RuntimeException getRuntimeException() {
      return this.throwable;
    }
  }

  static final class ErrorHolder {

    private final Error error;

    ErrorHolder(Error error) {
      this.error = error;
    }

    Error getError() {
      return this.error;
    }
  }

}
