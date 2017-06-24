package com.github.marschall.stiletto.tests.constructors;

import com.github.marschall.stiletto.api.generation.AdviseBy;
import com.github.marschall.stiletto.tests.BeforeCountingAspect;

@AdviseBy(BeforeCountingAspect.class)
public class AdvisedServiceWithConstructors {

  private final String s;

  private AdvisedServiceWithConstructors(String s) {
    this.s = s;
  }

  public AdvisedServiceWithConstructors(int i) {
    this(Integer.toString(i));
  }

  public AdvisedServiceWithConstructors(long l) {
    this(Long.toString(l));
  }

  public String simpleMethod() {
    return this.s;
  }

}
