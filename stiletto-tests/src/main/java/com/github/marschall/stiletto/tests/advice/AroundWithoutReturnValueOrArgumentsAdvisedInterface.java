package com.github.marschall.stiletto.tests.advice;

import com.github.marschall.stiletto.api.generation.AdviseBy;

@FunctionalInterface
@AdviseBy(AroundWithoutReturnValueOrArgumentsAspect.class)
public interface AroundWithoutReturnValueOrArgumentsAdvisedInterface {

  String simpleMethod(String argument);

}
