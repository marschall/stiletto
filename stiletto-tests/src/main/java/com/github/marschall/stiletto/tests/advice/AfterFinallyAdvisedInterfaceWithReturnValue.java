package com.github.marschall.stiletto.tests.advice;

import com.github.marschall.stiletto.api.generation.AdviseBy;

@FunctionalInterface
@AdviseBy(AfterFinallyAspectWithReturnValue.class)
public interface AfterFinallyAdvisedInterfaceWithReturnValue {

  String simpleMethod();

}
