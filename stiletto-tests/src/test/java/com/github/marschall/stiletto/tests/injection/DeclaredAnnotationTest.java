package com.github.marschall.stiletto.tests.injection;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class DeclaredAnnotationTest {

  private CaptureDeclaredAnnotationAspect aspect;
  private InjectAllAnnotationValues proxy;

  @BeforeEach
  public void setUp() {
    this.aspect = new CaptureDeclaredAnnotationAspect();
    InjectAllAnnotationValues targetObject = () -> {};
    this.proxy = new InjectAllAnnotationValues_(targetObject, this.aspect);
  }

  @Test
  public void allAnnotationValues() {
    this.proxy.method();

    AllAnnotationValues annotation = this.aspect.getAnnotation();
    assertNotNull(annotation);

    assertEquals(true, annotation.booleanValue());
    assertEquals(1, annotation.byteValue());
    assertEquals('2', annotation.charValue());
    assertEquals(3, annotation.shortValue());
    assertEquals(4, annotation.intValue());
    assertEquals(5, annotation.longValue());
    assertEquals(6.0f, annotation.floatValue(), 0.0001f);
    assertEquals(7.0d, annotation.doubleValue(), 0.0001d);
    assertEquals("8", annotation.stringValue());
    assertSame(BigDecimal.class, annotation.classValue());

    assertArrayEquals(new boolean[] {true, false}, annotation.booleanArrayValue());
    assertArrayEquals(new byte[] {9, 10}, annotation.byteArrayValue());
    assertArrayEquals(new char[] {'1', '2'}, annotation.charArrayValue());
    assertArrayEquals(new short[] {13, 14}, annotation.shortArrayValue());
    assertArrayEquals(new int[] {15, 16}, annotation.intArrayValue());
    assertArrayEquals(new long[] {17, 18}, annotation.longArrayValue());
    assertArrayEquals(new float[] {19, 20}, annotation.floatArrayValue(), 0.001f);
    assertArrayEquals(new double[] {21, 22}, annotation.doubleArrayValue(), 0.001d);
    assertArrayEquals(new String[] {"23", "24"}, annotation.stringArrayValue());
    assertArrayEquals(new Class[] {Integer.class, Long.class}, annotation.classArrayValue());

    // TODO better assertion
    assertNotNull(annotation.annotationValue());
    assertNotNull(annotation.annotationArrayValue());
  }

}
