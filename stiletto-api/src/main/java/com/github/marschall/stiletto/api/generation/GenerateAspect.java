package com.github.marschall.stiletto.api.generation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.SOURCE;

import static com.github.marschall.stiletto.api.generation.GenerateAspect.MethodSelection.ALL_PUBLIC;

import java.lang.annotation.Documented;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Documented
@Retention(SOURCE)
@Target(TYPE)
@Repeatable(GenerateAspects.class)
public @interface GenerateAspect {

  // TODO also method?
  // TODO name pattern expression?

  Class<?> value();

  MethodSelection defaultMethodSelection() default ALL_PUBLIC;

  enum MethodSelection {
    ALL,
    ALL_PUBLIC,
    NONE;
  }

}
