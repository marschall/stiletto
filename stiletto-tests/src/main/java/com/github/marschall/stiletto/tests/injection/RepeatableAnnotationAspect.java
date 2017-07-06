package com.github.marschall.stiletto.tests.injection;

import com.github.marschall.stiletto.api.advice.Before;
import com.github.marschall.stiletto.api.injection.DeclaredAnnotation;

public class RepeatableAnnotationAspect {

  private RepeatableContainer container;
  private RepeatableElement[] elements;

  @Before
  public void injectContainer(@DeclaredAnnotation RepeatableContainer container) {
    this.container = container;
  }

  @Before
  public void injectelements(@DeclaredAnnotation RepeatableElement[] elements) {
    this.elements = elements;
  }

  public RepeatableContainer getContainer() {
    return this.container;
  }

  public RepeatableElement[] getElements() {
    return this.elements;
  }

}
