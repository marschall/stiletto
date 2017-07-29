package com.github.marschall.stiletto.api.invocation;

public interface ChangeableMethodCall<R> {
  // TODO stupid name

  R invoke(Object[] arguments);

}
