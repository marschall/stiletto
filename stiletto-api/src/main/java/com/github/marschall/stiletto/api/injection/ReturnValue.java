package com.github.marschall.stiletto.api.injection;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Marks a parameter of as the return value of the execution of the
 * joinpoint method.
 */
@Retention(RUNTIME)
@Target(PARAMETER)
public @interface ReturnValue {

}
