package com.github.marschall.stiletto.tests.advice;

import com.github.marschall.stiletto.api.generation.AdviseBy;

@FunctionalInterface
@AdviseBy(AfterFinallyAspect.class)
public interface AfterFinallyAdvisedInterface {

  String simpleMethod();

}
