package com.github.marschall.stiletto.tests.injection;

public class RepeatableAnnotationService {

  @RepeatableElement("singleAnnoation")
  public void singleAnnoation() {

  }

  @RepeatableElement("annotation1")
  @RepeatableElement("annotation2")
  public void repeatedAnnoation() {

  }

  @RepeatableContainer({
    @RepeatableElement("inner1"),
    @RepeatableElement("inner2")
  })
  public void annoationConatiner() {

  }

}
