package com.github.marschall.stiletto.api.pointcut;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Documented
@Retention(RUNTIME)
@Target(METHOD)
public @interface MatchingExactly {

  // all attributes have to match

  Class<? extends Annotation>[] value() default {};

}
