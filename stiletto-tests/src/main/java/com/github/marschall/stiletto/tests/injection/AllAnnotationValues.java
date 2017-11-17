package com.github.marschall.stiletto.tests.injection;

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

  Class<? extends Number> classValue();

  boolean[] booleanArrayValue();

  byte[] byteArrayValue();

  char[] charArrayValue();

  short[] shortArrayValue();

  int[] intArrayValue();

  long[] longArrayValue();

  float[] floatArrayValue();

  double[] doubleArrayValue();

  String[] stringArrayValue();

  Class<? extends Number>[] classArrayValue();

  AfterReturning annotationValue();

  AfterReturning[] annotationArrayValue();

}
