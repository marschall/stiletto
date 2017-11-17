package com.github.marschall.stiletto.jperf;

import org.aspectj.lang.annotation.Aspect;

import net.jperf.LoggingStopWatch;
import net.jperf.aop.ProfiledTimingAspect;

@Aspect
public class NoopTimingAspect extends ProfiledTimingAspect {

  @Override
  protected LoggingStopWatch newStopWatch(String loggerName, String levelName) {
    return NoopLoggingStopWatch.INSTANCE;
  }

}
