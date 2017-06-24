package com.github.marschall.stiletto.tests.generic;

import com.github.marschall.stiletto.api.generation.AdviseBy;
import com.github.marschall.stiletto.tests.BeforeCountingAspect;

@AdviseBy(BeforeCountingAspect.class)
public class AdvisedNonGenericService implements GenericInterface<String> {

  @Override
  public String genericMethod() {
    return "ok";
  }

}
