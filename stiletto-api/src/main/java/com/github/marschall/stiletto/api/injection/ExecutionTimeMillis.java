package com.github.marschall.stiletto.api.injection;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Marks a parameter of type {@code long} to be the execution time in
 * milliseconds of the joinpoint method. Provides millisecond
 * precision, but not necessarily millisecond resolution.
 *
 * @see System#currentTimeMillis()
 */
@Retention(RUNTIME)
@Target(PARAMETER)
public @interface ExecutionTimeMillis {

}
