package com.github.marschall.stiletto.tests.advice;

import com.github.marschall.stiletto.api.generation.AdviseBy;

@FunctionalInterface
@AdviseBy(BeforeAspect.class)
public interface BeforeAdvisedInterface {

  String simpleMethod();

}
