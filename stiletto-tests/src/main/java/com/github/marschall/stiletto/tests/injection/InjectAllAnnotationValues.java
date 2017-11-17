package com.github.marschall.stiletto.tests.injection;

import java.math.BigDecimal;
import java.nio.file.StandardOpenOption;

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

          enumValue = StandardOpenOption.APPEND,

          booleanValueArray = {true, false},

          byteValueArray = {9, 10},

          charValueArray = {'1', '2'},

          shortValueArray = {13, 14},

          intValueArray = {15, 16},

          longValueArray = {17, 18},

          floatValueArray = {19, 20},

          doubleValueArray = {21, 22},

          stringValueArray = {"23", "24"},

          classValueArray = {Integer.class, Long.class},

          enumValueArray = {StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.DSYNC},

          annotationValue = @AfterReturning,

          annotationValueArray = {@AfterReturning, @AfterReturning}
  )
  void method();

}
