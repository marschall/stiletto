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
  public Object invoke(@DeclaredAnnotation Transactional transactional,
          @Evaluate("${targetClass.fullyQualifiedName}.${joinpoint.methodName}") String joinpointIdentification,
          @MethodCall ActualMethodCallWithResult<?> call) {

    TransactionDefinition definition = new TransactionalTransactionDefinition(transactional, joinpointIdentification);
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

  /**
   * Decide if we should roll back on an encountered exception.
   *
   * @param ex the encountered exception
   * @param transactional, the transactional annotation on the method
   *        or the class if none is present on the method
   * @return whether the transaction should be rolled back for the
   *         encountered exception
   * @see org.springframework.transaction.interceptor.RuleBasedTransactionAttribute#rollbackOn(Throwable)
   * @see org.springframework.transaction.interceptor.DefaultTransactionAttribute#rollbackOn(Throwable)
   * @see org.springframework.transaction.interceptor.TransactionAttribute#rollbackOn(Throwable)
   */
  private boolean rollbackOn(Throwable ex, Transactional transactional) {
    boolean rollback = false;
    boolean hasWin = false;
    int deepest = Integer.MAX_VALUE;

    int depth = getMatchDepth(ex, transactional.rollbackFor());
    if (depth >= 0) {
      deepest = depth;
      hasWin = true;
      rollback = true;
    }

    depth = getMatchDepth(ex, transactional.rollbackForClassName());
    if (depth >= 0 && depth < deepest) {
      deepest = depth;
      hasWin = true;
      rollback = true;
    }

    depth = getMatchDepth(ex, transactional.noRollbackFor());
    if (depth >= 0 && depth < deepest) {
      deepest = depth;
      hasWin = true;
      rollback = false;
    }

    depth = getMatchDepth(ex, transactional.noRollbackForClassName());
    if (depth >= 0 && depth < deepest) {
      deepest = depth;
      hasWin = true;
      rollback = false;
    }

    if (!hasWin) {
      rollback = ex instanceof RuntimeException || ex instanceof Error;
    }
    return rollback;
  }



  /*
   * @see org.springframework.transaction.interceptor.RollbackRuleAttribute#getDepth(Throwable)
   */
  private static int getMatchDepth(Throwable throwable, Class<? extends Throwable>[] matchClasses) {
    int deepest = Integer.MAX_VALUE;
    boolean win = false;
    for (Class<? extends Throwable> matchClass : matchClasses) {
      int depth = getMatchDepth(throwable, matchClass);
      if (depth >= 0) {
        win = true;
        deepest = Math.min(deepest, depth);
      }
    }
    if (win) {
      return deepest;
    } else {
      return -1;
    }
  }

  /*
   * @see org.springframework.transaction.interceptor.RollbackRuleAttribute#getDepth(Throwable)
   */
  private static int getMatchDepth(Throwable throwable, Class<? extends Throwable> matchClass) {
    if (!matchClass.isInstance(throwable)) {
      return -1;
    }
    int depth = 0;
    Class<?> current = throwable.getClass();
    while (true) {
      if (current == matchClass) {
        return depth;
      }
      depth += 1;
      current = current.getSuperclass();
    }
  }

  /*
   * @see org.springframework.transaction.interceptor.RollbackRuleAttribute#getDepth(Throwable)
   */
  private static int getMatchDepth(Throwable throwable, String[] patterns) {
    int deepest = Integer.MAX_VALUE;
    boolean win = false;
    for (String pattern : patterns) {
      int depth = getMatchDepth(throwable, pattern);
      if (depth > 0) {
        win = true;
        deepest = Math.min(deepest, depth);
      }
    }
    if (win) {
      return deepest;
    } else {
      return -1;
    }
  }

  /*
   * @see org.springframework.transaction.interceptor.RollbackRuleAttribute#getDepth(Throwable)
   */
  private static int getMatchDepth(Throwable throwable, String pattern) {
    int depth = 0;
    Class<?> current = throwable.getClass();
    while (current != Throwable.class) {
      if (current.getName().contains(pattern)) {
        return depth;
      }
      depth += 1;
      current = current.getSuperclass();
    }
    return -1;
  }

  /**
   * A {@link TransactionDefinition} delegating everything but the
   * joinpoint identification to a {@link Transactional}.
   */
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
