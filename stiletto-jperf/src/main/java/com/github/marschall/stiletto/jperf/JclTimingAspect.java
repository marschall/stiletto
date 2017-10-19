package com.github.marschall.stiletto.jperf;

import org.apache.commons.logging.LogFactory;

import net.jperf.LoggingStopWatch;
import net.jperf.commonslog.CommonsLogStopWatch;

/**
 *
 * @see net.jperf.commonslog.aop.TimingAspect
 */
public class JclTimingAspect extends AbstractJPerfAspect {

  @Override
  protected LoggingStopWatch newStopWatch(String loggerName, String levelName) {
    int levelInt = CommonsLogStopWatch.mapLevelName(levelName);
    return new CommonsLogStopWatch(LogFactory.getLog(loggerName), levelInt, levelInt);
}

}
