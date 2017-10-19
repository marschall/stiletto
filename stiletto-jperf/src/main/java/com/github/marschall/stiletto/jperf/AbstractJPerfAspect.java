package com.github.marschall.stiletto.jperf;

import java.lang.reflect.Method;

import com.github.marschall.stiletto.api.advice.Before;
import com.github.marschall.stiletto.api.injection.DeclaredAnnotation;
import com.github.marschall.stiletto.api.pointcut.Matching;

import net.jperf.LoggingStopWatch;
import net.jperf.aop.Profiled;
import net.jperf.helpers.JperfProperties;

/**
 *
 * @see net.jperf.aop.ProfiledTimingAspect
 * @see <a href="https://sovaa.github.io/jperf/devguide.html#Using_Spring_AOP_to_Integrate_Timing_Aspects">Using Spring AOP to Integrate Timing Aspects</a>
 */
public abstract class AbstractJPerfAspect {

  @Before
  @Profiled
  @Matching(Profiled.class)
  public LoggingStopWatch startStopWatc(@DeclaredAnnotation Profiled profiled) {

    String loggerName = profiled.logger();
    if (loggerName == null) {
      loggerName = "";
    }
    LoggingStopWatch stopWatch = newStopWatch(loggerName, profiled.level());

    if (!stopWatch.isLogging()) {
      //return joinPoint.proceed();
      return null;
    }

    long threshold = chooseThreshold(profiled);
    stopWatch.setTimeThreshold(threshold);
    stopWatch.setNormalAndSlowSuffixesEnabled(profiled.normalAndSlowSuffixesEnabled());

    return stopWatch;
  }

  // copy and pasted

  /**
   * Subclasses should implement this method to return a LoggingStopWatch that should be used to time the wrapped
   * code block.
   *
   * @param loggerName The name of the logger to use for persisting StopWatch messages.
   * @param levelName  The level at which the message should be logged.
   * @return The new LoggingStopWatch.
   */
  protected abstract LoggingStopWatch newStopWatch(String loggerName, String levelName);


  /**
   * Defaults to 0ms. First checks whether a non-negative value is set on the annotation, and if so will use it,
   * otherwise
   */
  private long chooseThreshold(Profiled profiled) {
    long thresholdFromAnnotation = profiled.timeThreshold();
    long thresholdFromConfig = getThresholdFromConfig();
    long thresholdDefaultValue = getThresholdDefaultValue(profiled);

    // if set on annotation, has higher priority than configured value
    if (thresholdFromAnnotation > thresholdDefaultValue) {
      return thresholdFromAnnotation;
    }
    if (thresholdFromConfig > thresholdDefaultValue) {
      return thresholdFromConfig;
    }

    // after this point, 0 is the default value; thresholdDefaultValue above is -1, just to make it possible to
    // set no threshold (0ms) to override a configured positive value
    return 0L;
  }

  private static final String DEFAULT_THRESHOLD_CONFIG_KEY = "net.jperf.threshold.default";

  /**
   * "Cache" the default annotation field value so we don't have to do reflection calls for every iteration; is not
   * changed between runs anyway.
   */
  private long getThresholdDefaultValue(Profiled profiled) {
    String thresholdString = JperfProperties.INSTANCE.getProperty(DEFAULT_THRESHOLD_CONFIG_KEY);
    if (thresholdString != null) {
      try {
        return Long.valueOf(thresholdString);
      }
      catch (NumberFormatException e) {
        // ignore
      }
    }
    Method timeThreshold;
    try {
      timeThreshold = profiled.annotationType().getMethod(Profiled.THRESHOLD_FIELD_NAME);
    } catch (NoSuchMethodException e1) {
      return -1L;
    }
    Long defaultValue = (Long) timeThreshold.getDefaultValue();
    if (defaultValue == null) {
      return -1L;
    }
    long thresholdDefault = (Long) defaultValue;
    JperfProperties.INSTANCE.setProperty(DEFAULT_THRESHOLD_CONFIG_KEY, String.valueOf(thresholdDefault));

    return -1;
  }

  private long getThresholdFromConfig() {
    String property = JperfProperties.INSTANCE.getProperty(Profiled.THRESHOLD_FIELD_NAME);
    if (property == null) {
      return -1;
    }
    try {
      return Long.parseLong(property);
    } catch (NumberFormatException e) {
      return -1;
    }
  }

}
