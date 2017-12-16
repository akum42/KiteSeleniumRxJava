package com;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.annotation.Id;

public class Stock {

  @Id private String stockName;
  private final Map<String, List<Double>> minuteAverage;
  private final List<Pair<String, Double>> fiveMinuteAverage;

  public Stock() {
    minuteAverage = new HashMap<>();
    fiveMinuteAverage = new ArrayList<>();
  }

  public String getStockName() {
    return stockName;
  }

  public void setStockName(String stockName) {
    this.stockName = stockName;
  }

  public Map<String, List<Double>> getMinuteAverage() {
    return minuteAverage;
  }

  public List<Pair<String, Double>> getFiveMinuteAverage() {
    return fiveMinuteAverage;
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
