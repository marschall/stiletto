package com.github.marschall.stiletto.tests.advice;

import com.github.marschall.stiletto.api.generation.AdviseBy;

@FunctionalInterface
@AdviseBy(AfterReturningAspectWithReturnValue.class)
public interface AfterReturningAdvisedInterfaceWithReturnValue {

  String simpleMethod();

}
