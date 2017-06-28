package com.github.marschall.stiletto.tests.advice;

import com.github.marschall.stiletto.api.generation.AdviseBy;

@FunctionalInterface
@AdviseBy(AfterReturningAspect.class)
public interface AfterReturningAdvisedInterface {

  String simpleMethod();

}
