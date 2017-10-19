package com.github.marschall.stiletto.jperf;

import net.jperf.LoggingStopWatch;
import net.jperf.aop.ProfiledTimingAspect;

public class NoopTimingAspect extends ProfiledTimingAspect {

  @Override
  protected LoggingStopWatch newStopWatch(String loggerName, String levelName) {
    return NoopLoggingStopWatch.INSTANCE;
  }


}
