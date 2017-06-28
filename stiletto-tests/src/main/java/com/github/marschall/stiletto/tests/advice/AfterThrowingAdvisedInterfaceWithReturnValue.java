package com.github.marschall.stiletto.tests.advice;

import com.github.marschall.stiletto.api.generation.AdviseBy;

@FunctionalInterface
@AdviseBy(AfterThrowingAspectWithReturnValue.class)
public interface AfterThrowingAdvisedInterfaceWithReturnValue {

  String simpleMethod();

}
