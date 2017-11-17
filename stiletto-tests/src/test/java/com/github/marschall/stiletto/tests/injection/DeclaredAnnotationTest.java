package com.github.marschall.stiletto.tests.injection;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigDecimal;
import java.nio.file.StandardOpenOption;

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
  public void allAnnotationValues() throws ClassNotFoundException, IOException {
    this.proxy.method();

    AllAnnotationValues annotation = this.aspect.getAnnotation();
    assertNotNull(annotation);
    assertSerialiable(annotation);

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
    assertSame(StandardOpenOption.APPEND, annotation.annotationValue());

    assertArrayEquals(new boolean[] {true, false}, annotation.booleanValueArray());
    assertArrayEquals(new byte[] {9, 10}, annotation.byteValueArray());
    assertArrayEquals(new char[] {'1', '2'}, annotation.charValueArray());
    assertArrayEquals(new short[] {13, 14}, annotation.shortValueArray());
    assertArrayEquals(new int[] {15, 16}, annotation.intValueArray());
    assertArrayEquals(new long[] {17, 18}, annotation.longValueArray());
    assertArrayEquals(new float[] {19, 20}, annotation.floatValueArray(), 0.001f);
    assertArrayEquals(new double[] {21, 22}, annotation.doubleValueArray(), 0.001d);
    assertArrayEquals(new String[] {"23", "24"}, annotation.stringValueArray());
    assertArrayEquals(new Class[] {Integer.class, Long.class}, annotation.classValueArray());
    assertArrayEquals(new StandardOpenOption[] {StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.DSYNC}, annotation.annotationValueArray());

    // TODO better assertion
    assertNotNull(annotation.annotationValue());
    assertNotNull(annotation.annotationValueArray());
  }

  private void assertSerialiable(Object obj) throws IOException, ClassNotFoundException {
    assertNotNull(obj);
    Object copy = serialiableCopy(obj);
    assertNotNull(copy);
    assertSame(obj.getClass(), copy.getClass());
  }

  private Object serialiableCopy(Object obj) throws IOException, ClassNotFoundException {
    ByteArrayOutputStream output = new ByteArrayOutputStream();
    try (ObjectOutputStream stream = new ObjectOutputStream(output)) {
      stream.writeObject(obj);
    }
    try (ByteArrayInputStream input = new ByteArrayInputStream(output.toByteArray());
         ObjectInputStream stream = new ObjectInputStream(input)) {
      return stream.readObject();
    }
  }

}
