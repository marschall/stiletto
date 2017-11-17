package com.github.marschall.stiletto.tests.injection;

import java.math.BigDecimal;

import com.github.marschall.stiletto.api.advice.AfterReturning;
import com.github.marschall.stiletto.api.generation.AdviseBy;

@AdviseBy(CaptureDeclaredAnnotationAspect.class)
public interface InjectAllAnnotationValues {

  @AllAnnotationValues(
          booleanValue = true,

          byteValue = 1,

          charValue = '2',

          shortValue = 3,

          intValue = 4,

          longValue = 5,

          floatValue = 6,

          doubleValue = 7,

          stringValue = "8",

          classValue = BigDecimal.class,

          booleanArrayValue = {true, false},

          byteArrayValue = {9, 10},

          charArrayValue = {'1', '2'},

          shortArrayValue = {13, 14},

          intArrayValue = {15, 16},

          longArrayValue = {17, 18},

          floatArrayValue = {19, 20},

          doubleArrayValue = {21, 22},

          stringArrayValue= {"23", "24"},

          classArrayValue = {Integer.class, Long.class},

          annotationValue = @AfterReturning,

          annotationArrayValue = {@AfterReturning, @AfterReturning}
  )
  void method();

}
