package com.github.marschall.stiletto.tests.generic;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.github.marschall.stiletto.tests.BeforeCountingAspect;

public class AdvisedNonGenericServiceTest {

  private BeforeCountingAspect aspect;

  @Before
  public void setUp() {
    this.aspect = new BeforeCountingAspect();
  }

  @Test
  public void genericMethod() {

    AdvisedNonGenericService targetObject = new AdvisedNonGenericService();

    AdvisedNonGenericService proxy = new AdvisedNonGenericService_(targetObject, this.aspect);

    assertEquals(0, this.aspect.getInvocationCount());
    assertEquals("ok", proxy.genericMethod());
    assertEquals(1, this.aspect.getInvocationCount());
  }

}
