package com.github.marschall.stiletto.api.injection;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Marks a parameter of as an annotation present either on the
 * joinpoint method or the target class.
 */
@Retention(RUNTIME)
@Target(PARAMETER)
public @interface DeclaredAnnotation {

  // TODO Optional<>? vs null
  // TODO make optional, infer from type

}
