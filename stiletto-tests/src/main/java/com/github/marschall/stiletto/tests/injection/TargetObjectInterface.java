package com.github.marschall.stiletto.tests.injection;

import com.github.marschall.stiletto.api.generation.AdviseBy;

@AdviseBy(TargetObjectAspect.class)
@FunctionalInterface
public interface TargetObjectInterface {

  void simpleMethod();

}
