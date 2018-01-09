package com;

import java.time.LocalTime;

public class Util {

  public static final void sleep(long millis) {
    try {
      Thread.sleep(millis);
    } catch (InterruptedException e) {
      System.out.println(e.getMessage());
    }
  }

  public static int orderPlaceTill() {
    return LocalTime.of(15, 10).compareTo(LocalTime.now())
        * LocalTime.now().compareTo(LocalTime.of(9, 15));
  }

  public static int marketOpenTill() {
    return LocalTime.of(15, 29).compareTo(LocalTime.now())
        * LocalTime.now().compareTo(LocalTime.of(9, 15));
  }
}
