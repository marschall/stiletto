package com.github.marschall.stiletto.api.injection;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Marks a parameter of type {@link java.lang.reflect.Method} as the
 * joinpoint method.
 */
@Retention(RUNTIME)
@Target(PARAMETER)
public @interface Joinpoint {

  // TODO make optional, infer from type

}
