package com.github.marschall.stiletto.api.injection;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Marks a parameter of type {@code long} to be the execution time in
 * nanoseconds of the joinpoint method. Provides nanosecond precision,
 * but not necessarily nanosecond resolution.
 *
 * @see <a href="https://shipilev.net/blog/2014/nanotrusting-nanotime/">Nanotrusting the Nanotime</a>
 * @see System#nanoTime()
 */
@Retention(RUNTIME)
@Target(PARAMETER)
public @interface ExecutionTimeNanos {

}
