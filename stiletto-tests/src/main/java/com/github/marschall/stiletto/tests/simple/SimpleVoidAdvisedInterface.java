package com.github.marschall.stiletto.tests.simple;

import com.github.marschall.stiletto.api.generation.AdviseBy;
import com.github.marschall.stiletto.tests.BeforeCountingAspect;

@FunctionalInterface
@AdviseBy(BeforeCountingAspect.class)
public interface SimpleVoidAdvisedInterface {

  void simpleMethod();

}
