package com.github.marschall.stiletto.api.injection;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.github.marschall.stiletto.api.advice.Before;

/**
 * Marks a parameter of as the return value of the execution of a
 * an aspect method annotated with {@link Before}.
 */
@Retention(RUNTIME)
@Target(PARAMETER)
public @interface BeforeValue {

  // TODO better name

}
