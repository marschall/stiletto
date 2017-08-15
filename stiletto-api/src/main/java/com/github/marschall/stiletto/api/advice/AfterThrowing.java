package com.github.marschall.stiletto.api.advice;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Documented
@Retention(RUNTIME)
@Target(METHOD)
public @interface AfterThrowing {

  // TODO default Exception?
  // TODO could in theory be inferred from @Thrown
  Class<? extends Throwable>[] value() default {RuntimeException.class, Error.class};

  // TODO
//  boolean onDeclared() default false;

}
