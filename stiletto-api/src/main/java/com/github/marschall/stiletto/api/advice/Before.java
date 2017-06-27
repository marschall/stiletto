package com.github.marschall.stiletto.api.advice;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Advice that executes before a join point (method execution), but
 * which does not have the ability to prevent execution flow proceeding
 * to the join point (unless it throws an exception).
 * <p>
 * A method annotated with this annotation must be {@code void}.
 */
@Documented
@Retention(RUNTIME)
@Target(METHOD)
public @interface Before {

}
