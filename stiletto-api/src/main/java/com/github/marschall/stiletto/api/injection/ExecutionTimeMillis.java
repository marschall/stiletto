package com.github.marschall.stiletto.api.injection;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.github.marschall.stiletto.api.advice.AfterFinally;
import com.github.marschall.stiletto.api.advice.AfterReturning;
import com.github.marschall.stiletto.api.advice.AfterThrowing;

/**
 * Marks a parameter of type {@code long} to be the execution time in
 * milliseconds of the joinpoint method. Provides millisecond
 * precision, but not necessarily millisecond resolution.
 * <p>
 * Can be used in methods annotated with {@link AfterReturning},
 * {@link AfterThrowing} or {@link AfterFinally}.
 *
 * @see System#currentTimeMillis()
 */
@Retention(RUNTIME)
@Target(PARAMETER)
public @interface ExecutionTimeMillis {

}
