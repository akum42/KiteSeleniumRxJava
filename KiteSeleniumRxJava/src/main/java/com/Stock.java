package com;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.data.annotation.Id;

public class Stock {

  @Id private String stockName;
  private final Map<String, Double> minuteAverage;
  private final Map<String, Double> fiveMinuteAverage;
  private final Map<String, Double> fastMovingAverage;
  private final Map<String, Double> slowMovingAverage;

  public Stock() {
    minuteAverage = new ConcurrentHashMap<>();
    fiveMinuteAverage = new ConcurrentHashMap<>();
    fastMovingAverage = new ConcurrentHashMap<>();
    slowMovingAverage = new ConcurrentHashMap<>();
  }

  public Map<String, Double> getFastMovingAverage() {
    return fastMovingAverage;
  }

  public Map<String, Double> getSlowMovingAverage() {
    return slowMovingAverage;
  }

  public Map<String, Double> getFiveMinuteAverage() {
    return fiveMinuteAverage;
  }

  public Map<String, Double> getMinuteAverage() {
    return minuteAverage;
  }

  public String getStockName() {
    return stockName;
  }

  public void setStockName(String stockName) {
    this.stockName = stockName;
  }

  @Override
  public String toString() {
    return "Stock [stockName="
        + stockName
        + ", minuteAverage="
        + minuteAverage
        + ", fiveMinuteAverage="
        + fiveMinuteAverage
        + ", fastMovingAverage="
        + fastMovingAverage
        + ", slowMovingAverage="
        + slowMovingAverage
        + "]";
  }
}
