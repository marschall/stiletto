package com.github.marschall.stiletto.javasimon;

import org.javasimon.aop.Monitored;

import com.github.marschall.stiletto.api.advice.Before;
import com.github.marschall.stiletto.api.injection.DeclaredAnnotation;
import com.github.marschall.stiletto.api.pointcut.Matching;

public class SimonAspect {

  @Before
  @Matching(Monitored.class)
  public void start(@DeclaredAnnotation Monitored monitored) {

  }

}
