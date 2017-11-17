package com.github.marschall.stiletto.tests.injection;

import com.github.marschall.stiletto.api.advice.Before;
import com.github.marschall.stiletto.api.injection.DeclaredAnnotation;

public class CaptureDeclaredAnnotationAspect {

  private AllAnnotationValues annotation;

  @Before
  public void injectContainer(@DeclaredAnnotation AllAnnotationValues annotation) {
    this.annotation = annotation;
  }

  public AllAnnotationValues getAnnotation() {
    return this.annotation;
  }

}
