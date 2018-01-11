package com;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.data.annotation.Id;

public class Stock {

  @Id private String stockName;
  private final Map<String, Double> minuteAverage;
  private final Map<String, Double> fiveMinuteAverage;

  public Stock() {
    minuteAverage = new ConcurrentHashMap<>();
    fiveMinuteAverage = new ConcurrentHashMap<>();
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
        + "]";
  }
}
