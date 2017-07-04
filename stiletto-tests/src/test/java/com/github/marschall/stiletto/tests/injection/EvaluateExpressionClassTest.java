package com.github.marschall.stiletto.tests.injection;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

public class EvaluateExpressionClassTest {

  private EvaluateAspect aspect;
  private EvaluateExpressionClass proxy;

  @Before
  public void setUp() {
    this.aspect = new EvaluateAspect();
    EvaluateExpressionClass targetObject = new EvaluateExpressionClass();
    this.proxy = new EvaluateExpressionClass_(targetObject, this.aspect);
  }

  @Test
  public void noArgument() {
    this.proxy.noArguments();
    assertEquals("prefix " + EvaluateExpressionClass.class.getName() + ".noArguments()", this.aspect.getValue());
  }

  @Test
  public void oneArgument() {
    assertEquals("ok", this.proxy.oneArgument("ok"));
    assertEquals("prefix " + EvaluateExpressionClass.class.getName() + ".oneArgument(java.lang.String)", this.aspect.getValue());
  }

  @Test
  public void twoArguments() {
    assertEquals(3, this.proxy.twoArguments(1, 2));
    assertEquals("prefix " + EvaluateExpressionClass.class.getName() + ".twoArguments(int, int)", this.aspect.getValue());
  }

}
