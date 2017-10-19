package com.github.marschall.stiletto.jperf;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import net.jperf.LoggingStopWatch;
import net.jperf.log4j.Log4JStopWatch;

/**
 *
 * @see net.jperf.log4j.aop.TimingAspect
 *
 */
public final class Log4jTimingAspect extends AbstractJPerfAspect {

  @Override
  protected LoggingStopWatch newStopWatch(String loggerName, String levelName) {
      Level level = Level.toLevel(levelName, Level.INFO);
      return new Log4JStopWatch(Logger.getLogger(loggerName), level, level);
  }

}
