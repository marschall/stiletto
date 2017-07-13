package com.github.marschall.stiletto.tests.injection;

import static org.junit.Assert.assertEquals;

import java.util.Collections;

import org.junit.Before;
import org.junit.Test;

public class EvaluateExpressionTest {

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

  @Test
  public void genericMethod() {
    assertEquals(Integer.valueOf(1), this.proxy.genericMethod(1));
    assertEquals("prefix " + EvaluateExpressionClass.class.getName() + ".genericMethod(java.lang.Number)", this.aspect.getValue());
  }

  @Test
  public void erasure() {
    assertEquals(1, this.proxy.erasure(Collections.singletonList("1")));
    assertEquals("prefix " + EvaluateExpressionClass.class.getName() + ".erasure(java.util.List)", this.aspect.getValue());
  }

  @Test
  public void array() {
    assertEquals(3, this.proxy.array(new String[] {"a", "2", ")"}));
    assertEquals("prefix " + EvaluateExpressionClass.class.getName() + ".array(java.lang.String[])", this.aspect.getValue());
  }

}
