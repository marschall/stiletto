package com.github.marschall.stiletto.tests.injection;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class ExecutionTimeNanosTest {

  @Test
  public void executionTime() {
    CaptureExecutionTimeNanosAspect aspect = new CaptureExecutionTimeNanosAspect();
    InjectExecutionTimeNanos targetObject = () -> {
      try {
        Thread.sleep(0L, 10);
      } catch (InterruptedException e) {
        throw new RuntimeException("interrupted", e);
      }
      return "return value";
    };
    InjectExecutionTimeNanos proxy = new InjectExecutionTimeNanos_(targetObject, aspect);

    assertEquals("return value", proxy.method());
    assertThat(aspect.getNanos(), greaterThanOrEqualTo(10L));
  }

}
