package com.github.marschall.stiletto.jperf;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;

import net.jperf.LoggingStopWatch;

/**
 *
 * @see net.jperf.log4j.aop.TimingAspect
 *
 */
public final class Log4j2TimingAspect extends AbstractJPerfAspect {

  @Override
  protected LoggingStopWatch newStopWatch(String loggerName, String levelName) {
      Level level = Level.toLevel(levelName, Level.INFO);
      return new Log4J2StopWatch(LogManager.getLogger(loggerName), level, level);
  }

}
