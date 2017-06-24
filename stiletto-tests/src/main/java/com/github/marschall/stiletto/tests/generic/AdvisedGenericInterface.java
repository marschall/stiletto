package com.github.marschall.stiletto.tests.generic;

import com.github.marschall.stiletto.api.generation.AdviseBy;
import com.github.marschall.stiletto.tests.BeforeCountingAspect;

@FunctionalInterface
@AdviseBy(BeforeCountingAspect.class)
public interface AdvisedGenericInterface<T> {

  T genericMethod();

}
