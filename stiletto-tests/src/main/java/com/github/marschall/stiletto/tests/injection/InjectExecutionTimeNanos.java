package com.github.marschall.stiletto.tests.injection;

import com.github.marschall.stiletto.api.generation.AdviseBy;

@FunctionalInterface
@AdviseBy(CaptureExecutionTimeNanosAspect.class)
public interface InjectExecutionTimeNanos {

  String method();

}
