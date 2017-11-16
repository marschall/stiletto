package com.github.marschall.stiletto.tests.injection;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class ExecutionTimeMillisTest {

  @Test
  public void executionTime() {
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
