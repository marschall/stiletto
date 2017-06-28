package com.github.marschall.stiletto.tests.advice;

import com.github.marschall.stiletto.api.generation.AdviseBy;

@FunctionalInterface
@AdviseBy(AfterThrowingAspect.class)
public interface AfterThrowingAdvisedInterface {

  String simpleMethod();

}
