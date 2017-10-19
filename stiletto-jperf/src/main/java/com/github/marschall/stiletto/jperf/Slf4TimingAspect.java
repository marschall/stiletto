package com.github.marschall.stiletto.jperf;

import org.slf4j.LoggerFactory;

import net.jperf.LoggingStopWatch;
import net.jperf.slf4j.Slf4JStopWatch;

/**
 *
 * @see net.jperf.slf4j.aop.TimingAspect
 */
public final class Slf4TimingAspect extends AbstractJPerfAspect {

  @Override
  protected LoggingStopWatch newStopWatch(String loggerName, String levelName) {
    int levelInt = Slf4JStopWatch.mapLevelName(levelName);
    return new Slf4JStopWatch(LoggerFactory.getLogger(loggerName), levelInt, levelInt);
}

}
