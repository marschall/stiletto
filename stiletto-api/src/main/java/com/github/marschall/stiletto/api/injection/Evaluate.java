package com.github.marschall.stiletto.api.injection;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Allows to define an expression in
 * <a href="https://en.wikipedia.org/wiki/Unified_Expression_Language">Unified Expression Language</a>
 * that is at compile time an injected into an advice as a method parameter.
 * Currently {@link String} is the only type of value supported.
 *
 * <p>
 * The currently available properties are:
 * <dl>
 *  <dt><code>${targetClass.fullyQualifiedName}</code></dt>
 *  <dd>The fully qualified name of the target class, includes the package name.</dd>
 *  <dt><code>${targetClass.simpleName}</code></dt>
 *  <dd>The simple name of the target class, does not include the package name.</dd>
 *  <dt><code>${joinpoint.methodName}</code></dt>
 *  <dd>The name of the join point method.</dd>
 *  <dt><code>${joinpoint.methodSignature}</code></dt>
 *  <dd>The <a href="https://docs.oracle.com/javase/tutorial/java/javaOO/methods.html">signature</a>
 *  of the join point method, includes the name of the method as well as the parameter types.</dd>
 * </dl>
 *
 * <h2>Examples</h2>
 * For example the following can be used to do trace logging without any allocation
 * <pre><code>  @Before
 *  public void traceEntering(@Evaluate("entering ${targetClass.fullyQualifiedName}.${joinpoint.methodSignature}") String logMessage) {
 *    System.out.println(logMessage);
 *  }
 * </code></pre>
 *
 *
 * @see <a href="https://jcp.org/en/jsr/detail?id=341">JSR-341</a>
 */
@Documented
@Retention(RUNTIME)
@Target(PARAMETER)
public @interface Evaluate {

  /**
   * Defines the expression to evaluate at compile time.
   *
   * @return the expression to evaluate
   */
  String value();

}
