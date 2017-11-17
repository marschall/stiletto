package com.github.marschall.stiletto.tests.injection;

import java.nio.file.StandardOpenOption;

import com.github.marschall.stiletto.api.advice.AfterReturning;

public @interface AllAnnotationValues {

  boolean booleanValue();

  byte byteValue();

  char charValue();

  short shortValue();

  int intValue();

  long longValue();

  float floatValue();

  double doubleValue();

  String stringValue();

  StandardOpenOption enumValue();

  Class<? extends Number> classValue();

  boolean[] booleanValueArray();

  byte[] byteValueArray();

  char[] charValueArray();

  short[] shortValueArray();

  int[] intValueArray();

  long[] longValueArray();

  float[] floatValueArray();

  double[] doubleValueArray();

  String[] stringValueArray();

  Class<? extends Number>[] classValueArray();

  StandardOpenOption[] enumValueArray();

  AfterReturning annotationValue();

  AfterReturning[] annotationValueArray();

}
