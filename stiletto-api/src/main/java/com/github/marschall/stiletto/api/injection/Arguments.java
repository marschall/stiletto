package com.github.marschall.stiletto.api.injection;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 *
 * @see java.lang.reflect.InvocationHandler#invoke(Object, Method, Object[])
 */
@Documented
@Retention(RUNTIME)
@Target(PARAMETER)
public @interface Arguments {

}
