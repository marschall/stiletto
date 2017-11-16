package com.github.marschall.stiletto.tests.injection;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.arrayWithSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class RepeatableAnnotationServiceTest {

  private RepeatableAnnotationAspect aspect;
  private RepeatableAnnotationService proxy;

  @BeforeEach
  public void setUp() {
    this.aspect = new RepeatableAnnotationAspect();
    RepeatableAnnotationService targetObject = new RepeatableAnnotationService();
    // FIXME
//    this.proxy = new RepeatableAnnotationService_(targetObject, this.aspect);
    this.proxy = targetObject;
  }

  @Test
  public void singleAnnoation() {
    this.proxy.singleAnnoation();

    RepeatableContainer container = this.aspect.getContainer();
    assertNotNull(container);
    assertThat(container.value(), arrayWithSize(1));
    assertEquals("singleAnnoation", container.value()[0]);

    RepeatableElement[] elements = this.aspect.getElements();
    assertNotNull(elements);
    assertThat(elements, arrayWithSize(1));
    assertEquals("singleAnnoation", elements[0]);
  }

  @Test
  public void repeatedAnnoation() {
    this.proxy.repeatedAnnoation();

    RepeatableContainer container = this.aspect.getContainer();
    assertNotNull(container);
    assertThat(container.value(), arrayWithSize(2));
    assertEquals("annotation1", container.value()[0]);
    assertEquals("annotation2", container.value()[1]);

    RepeatableElement[] elements = this.aspect.getElements();
    assertNotNull(elements);
    assertThat(elements, arrayWithSize(2));
    assertEquals("annotation1", elements[0]);
    assertEquals("annotation2", elements[1]);
  }

  @Test
  public void annoationConatiner() {
    this.proxy.annoationConatiner();

    RepeatableContainer container = this.aspect.getContainer();
    assertNotNull(container);
    assertThat(container.value(), arrayWithSize(2));
    assertEquals("inner1", container.value()[0]);
    assertEquals("inner2", container.value()[1]);

    RepeatableElement[] elements = this.aspect.getElements();
    assertNotNull(elements);
    assertThat(elements, arrayWithSize(2));
    assertEquals("inner1", elements[0]);
    assertEquals("inner2", elements[1]);
  }

}
