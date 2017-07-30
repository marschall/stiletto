package com.github.marschall.stiletto.api.advice;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Advice to be executed after a join point (method) returns without
 * throwing an exception.
 * <p>
 * If a method annotated with this has a return value then this is used
 * as the return value of the method.
 */
@Documented
@Retention(RUNTIME)
@Target(METHOD)
public @interface AfterReturning {

    // if void result is not changed
    // different annotation?

  // TODO allow changing return value?
  // TODO pass null for void? use optional for return value instead

}
