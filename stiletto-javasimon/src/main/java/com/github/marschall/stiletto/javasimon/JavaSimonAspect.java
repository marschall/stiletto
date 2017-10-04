package com.github.marschall.stiletto.javasimon;

import org.javasimon.Manager;
import org.javasimon.SimonManager;
import org.javasimon.Split;
import org.javasimon.aop.Monitored;

import com.github.marschall.stiletto.api.advice.AfterReturning;
import com.github.marschall.stiletto.api.advice.AfterThrowing;
import com.github.marschall.stiletto.api.advice.Before;
import com.github.marschall.stiletto.api.injection.BeforeValue;
import com.github.marschall.stiletto.api.injection.DeclaredAnnotation;
import com.github.marschall.stiletto.api.injection.Evaluate;
import com.github.marschall.stiletto.api.injection.Thrown;
import com.github.marschall.stiletto.api.pointcut.Matching;

public final class JavaSimonAspect {

  /**
   * Default tag for exception sub monitors.
   */
  public static final String EXCEPTION_TAG = "failed";

  /**
   * Simon manager used for producing Stopwatches.
   */
  private final Manager manager;

  private final boolean tagByExceptionType;

  /**
   * Constructor with {@link Manager}.
   * <p>
   * also sets whether all exceptions should report to
   * {@link #EXCEPTION_TAG} sub-simon or sub-simon
   * for each exception type should be introduced (based on exception's
   * simple name).
   *
   * @param manager Simon manager used for producing Stopwatches
   * @param tagByExceptionType {@code true} for fine grained exception-class-name-based sub-simons
   */
  public JavaSimonAspect(Manager manager, boolean tagByExceptionType) {
    this.manager = manager;
    this.tagByExceptionType = tagByExceptionType;
  }

  /**
   * Constructor with {@link Manager}.
   *
   * @param manager Simon manager used for producing Stopwatches
   */
  public JavaSimonAspect(Manager manager) {
    this(manager, false);
  }

  /**
   * Default constructor using {@link SimonManager#manager()}.
   */
  public JavaSimonAspect() {
    this(SimonManager.manager());
  }

  @Before
  @Monitored
  @Matching(Monitored.class)
  public Split start(
          @DeclaredAnnotation Monitored monitored,
          @Evaluate("${targetClass.fullyQualifiedName}.${joinpoint.methodName}") String defaultMonitorName) {
    String configuredName = monitored.name();
    String monitorName = configuredName.isEmpty() ? defaultMonitorName : configuredName;
    return this.manager.getStopwatch(monitorName).start();
  }

  @AfterReturning
  @Monitored
  @Matching(Monitored.class)
  public Split stop(@BeforeValue Split split) {
    return split.stop();
  }

  @AfterThrowing(RuntimeException.class)
  @Monitored
  @Matching(Monitored.class)
  public void onException(
          @BeforeValue Split split,
          @Thrown RuntimeException e) {
    split.stop(this.tagByExceptionType ? e.getClass().getSimpleName() : EXCEPTION_TAG);
    throw e;
  }

  @AfterThrowing(Error.class)
  @Monitored
  @Matching(Monitored.class)
  public void onError(
          @BeforeValue Split split,
          @Thrown Error e) {
    split.stop(this.tagByExceptionType ? e.getClass().getSimpleName() : EXCEPTION_TAG);
    throw e;
  }

}
