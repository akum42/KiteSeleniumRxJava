package com;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.data.annotation.Id;

public class Stock {

  @Id private String stockName;
  private final Map<String, List<Double>> minuteAverage;
  private final List<Pair<String, Double>> fiveMinuteAverage;

  public Stock() {
    minuteAverage = new ConcurrentHashMap<>();
    fiveMinuteAverage = new ArrayList<>();
  }

  public List<Pair<String, Double>> getFiveMinuteAverage() {
    return fiveMinuteAverage;
  }

  public Map<String, List<Double>> getMinuteAverage() {
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
