package com;

import java.time.LocalTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Util {
  private static final Logger logger = LoggerFactory.getLogger(Util.class);

  public static boolean isMarketOpen() {
    return LocalTime.of(15, 29).compareTo(LocalTime.now())
                * LocalTime.now().compareTo(LocalTime.of(9, 15))
            > 0
        ? true
        : false;
  }

  public static boolean canOrder() {
    return LocalTime.of(15, 10).compareTo(LocalTime.now())
                * LocalTime.now().compareTo(LocalTime.of(9, 15))
            > 0
        ? true
        : false;
  }

  public static final void sleep(long millis) {
    try {
      Thread.sleep(millis);
    } catch (InterruptedException e) {
      logger.error(e.getMessage());
    }
  }
}
