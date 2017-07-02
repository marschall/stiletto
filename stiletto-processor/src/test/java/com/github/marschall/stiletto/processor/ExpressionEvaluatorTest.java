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
    JoinPoint joinPoint = new JoinPoint("equals", "equals(Object)");
    assertEquals("java.lang.Object.equals", expressionEvaluator.evaluate("${targetClass.fullyQualifiedName}.${joinpoint.methodName}", targetClass, joinPoint));

    assertEquals("Object.equals", expressionEvaluator.evaluate("${targetClass.simpleName}.${joinpoint.methodName}", targetClass, joinPoint));

    assertEquals("Object.equals(Object)", expressionEvaluator.evaluate("${targetClass.simpleName}.${joinpoint.methodSignature}", targetClass, joinPoint));

    assertEquals("entering java.lang.Object.equals", expressionEvaluator.evaluate("entering ${targetClass.fullyQualifiedName}.${joinpoint.methodName}", targetClass, joinPoint));
  }

}
