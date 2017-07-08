package com.github.marschall.stiletto.tests.injection;

import com.github.marschall.stiletto.api.generation.AdviseBy;

@AdviseBy(CaptureReturnValueAspect.class)
public interface InjectReturnValue {

  String method();

}
