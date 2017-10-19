package com.github.marschall.stiletto.jperf;

import java.util.logging.Level;
import java.util.logging.Logger;

import net.jperf.LoggingStopWatch;
import net.jperf.javalog.JavaLogStopWatch;

/**
 *
 * @see net.jperf.javalog.aop.TimingAspect
 */
public class JulTimingAspect extends AbstractJPerfAspect {

  @Override
  protected LoggingStopWatch newStopWatch(String loggerName, String levelName) {
    Level level = JavaLogStopWatch.mapLevelName(levelName);
    return new JavaLogStopWatch(Logger.getLogger(loggerName), level, level);
}

}
