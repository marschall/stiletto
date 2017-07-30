package com.github.marschall.stiletto.spring;

import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.CallbackPreferringPlatformTransactionManager;

/**
 * Abstract base class for transaction aspects using either a
 * {@link PlatformTransactionManager} or a
 * {@link CallbackPreferringPlatformTransactionManager}.
 */
abstract class AbstractTransactionalAspect {

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
  protected static boolean rollbackOn(Throwable ex, Transactional transactional) {
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
      // default behavior if no rule was found
      rollback = ex instanceof RuntimeException || ex instanceof Error;
    }
    return rollback;
  }

  protected TransactionalTransactionDefinition newTransactionDefinition(Transactional transactional, String joinpointIdentification) {
    return new TransactionalTransactionDefinition(transactional, joinpointIdentification);
  }

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

  private static int getMatchDepth(Throwable throwable, String[] patterns) {
    int deepest = Integer.MAX_VALUE;
    boolean win = false;
    for (String pattern : patterns) {
      int depth = getMatchDepth(throwable, pattern);
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
  protected static final class TransactionalTransactionDefinition implements TransactionDefinition {

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