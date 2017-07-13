package com.github.marschall.stiletto.api.injection;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.github.marschall.stiletto.api.advice.AfterFinally;
import com.github.marschall.stiletto.api.advice.AfterReturning;
import com.github.marschall.stiletto.api.advice.AfterThrowing;

/**
 * Marks a parameter of type {@code long} to be the execution time in
 * nanoseconds of a method. Provides nanosecond precision, but not
 * necessarily nanosecond resolution.
 * <p>
 * Can be used in methods annotated with {@link AfterReturning},
 * {@link AfterThrowing} or {@link AfterFinally}.
 *
 * @see <a href="https://shipilev.net/blog/2014/nanotrusting-nanotime/">Nanotrusting the Nanotime</a>
 * @see System#nanoTime()
 */
@Documented
@Retention(RUNTIME)
@Target(PARAMETER)
public @interface ExecutionTimeNanos {

}
