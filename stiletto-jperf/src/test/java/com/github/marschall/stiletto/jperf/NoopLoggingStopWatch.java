package com.github.marschall.stiletto.jperf;

import net.jperf.LoggingStopWatch;

final class NoopLoggingStopWatch extends LoggingStopWatch {

  static final LoggingStopWatch INSTANCE = new NoopLoggingStopWatch();

  NoopLoggingStopWatch() {
    super();
  }

  NoopLoggingStopWatch(String tag) {
    super(tag);
  }

  NoopLoggingStopWatch(String tag, String message) {
    super(tag, message);
  }

  NoopLoggingStopWatch(long startTime, long elapsedTime, String tag, String message) {
    super(startTime, elapsedTime, tag, message);
  }

  @Override
  protected void log(String stopWatchAsString, Throwable exception) {
    // noop
  }

}
