package com.github.marschall.stiletto.api.generation;

import static com.github.marschall.stiletto.api.generation.AdviseBy.MethodSelection.ALL_PUBLIC;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.SOURCE;

import java.lang.annotation.Documented;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Documented
@Retention(SOURCE)
@Target(TYPE)
@Repeatable(AdviseByAll.class)
public @interface AdviseBy {

  // configures the advisor
  // TODO also method?
  // TODO name pattern expression?
  // TODO on aspect instead? probably no, aspect should stay generic


  Class<?> value();

  MethodSelection defaultMethodSelection() default ALL_PUBLIC;

  enum MethodSelection {
    ALL,
    ALL_PUBLIC,
    NONE;
  }

}
