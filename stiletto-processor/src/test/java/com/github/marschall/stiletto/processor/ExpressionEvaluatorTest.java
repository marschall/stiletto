package com.github.marschall.stiletto.processor;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.github.marschall.stiletto.processor.el.JoinPoint;
import com.github.marschall.stiletto.processor.el.TargetClass;

public class ExpressionEvaluatorTest {

  private ExpressionEvaluator expressionEvaluator;

  @Before
  public void setUp() {
    this.expressionEvaluator = new ExpressionEvaluator();
  }

  @Test
  public void eval() {
    TargetClass targetClass = new TargetClass("java.lang.Object");
    JoinPoint joinPoint = new JoinPoint("toString");
    assertEquals("java.lang.Object.toString", expressionEvaluator.evaluate("${targetClass.name}.${joinpoint.methodName}", targetClass, joinPoint));

    assertEquals("Object.toString", expressionEvaluator.evaluate("${targetClass.simpleName}.${joinpoint.methodName}", targetClass, joinPoint));
  }

}
