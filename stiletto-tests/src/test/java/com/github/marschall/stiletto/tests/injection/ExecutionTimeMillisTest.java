package com.github.marschall.stiletto.tests.injection;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class ExecutionTimeMillisTest {

  @Test
  public void noArgument() {
    CaptureExecutionTimeMillisAspect aspect = new CaptureExecutionTimeMillisAspect();
    InjectExecutionTimeMillis targetObject = () -> {
      try {
        Thread.sleep(10L);
      } catch (InterruptedException e) {
        throw new RuntimeException("interrupted", e);
      }
      return "return value";
    };
    InjectExecutionTimeMillis proxy = new InjectExecutionTimeMillis_(targetObject, aspect);

    assertEquals("return value", proxy.method());
    assertThat(aspect.getMillis(), greaterThanOrEqualTo(10L));
  }

}
