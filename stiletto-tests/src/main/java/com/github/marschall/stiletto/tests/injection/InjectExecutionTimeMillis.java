package com.github.marschall.stiletto.tests.injection;

import com.github.marschall.stiletto.api.generation.AdviseBy;

@FunctionalInterface
@AdviseBy(CaptureExecutionTimeMillisAspect.class)
public interface InjectExecutionTimeMillis {

  String method();

}
