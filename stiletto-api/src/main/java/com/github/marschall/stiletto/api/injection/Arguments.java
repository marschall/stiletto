package com.github.marschall.stiletto.api.injection;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.reflect.Method;

/**
 *
 * @see java.lang.reflect.InvocationHandler#invoke(Object, Method, Object[])
 */
@Retention(RUNTIME)
@Target(PARAMETER)
public @interface Arguments {

}
