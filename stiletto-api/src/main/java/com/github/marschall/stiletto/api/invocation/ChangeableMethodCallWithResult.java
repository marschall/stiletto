package com.github.marschall.stiletto.api.invocation;

public interface ChangeableMethodCallWithResult<R> {
  // TODO stupid name

  R invoke(Object[] arguments);

}
