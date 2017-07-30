package com.github.marschall.stiletto.tests.injection;

import java.time.LocalDate;

import com.github.marschall.stiletto.api.generation.AdviseBy;

@AdviseBy(NameClashAspect.class)
public class NameClash {

  public String method(LocalDate returnValue, String exectionTimeMillis, String exectionTimeNanos) {
    return returnValue + " " + exectionTimeMillis + " " + exectionTimeNanos;
  }

}
