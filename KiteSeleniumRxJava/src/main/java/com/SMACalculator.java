package com;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.Subject;

@Service
public class SMACalculator {
  @Autowired private StockRepository repository;
  private final Subject<Pair<String, Double>> sma_1_min;
  private final Subject<Pair<String, Double>> sma_5_min;
  private final Subject<Pair<String, Double>> sma_slow;
  private final Subject<Pair<String, Double>> sma_fast;
  private final Map<String, Subject<Pair<Double, Double>>> stockSMASlowPair;
  private final Map<String, Subject<Pair<Double, Double>>> stockSMAFastPair;

  private final int slow = 31;
  private final int fast = 3;
  private final int slot = 5;

  public SMACalculator() {
    sma_1_min = BehaviorSubject.create();
    sma_5_min = BehaviorSubject.create();
    sma_slow = BehaviorSubject.create();
    sma_fast = BehaviorSubject.create();
    stockSMASlowPair = new ConcurrentHashMap<>();
    stockSMAFastPair = new ConcurrentHashMap<>();
  }

  public void complete() {
    sma_1_min.onComplete();
    sma_5_min.onComplete();
    sma_slow.onComplete();
    sma_fast.onComplete();
  }

  private void calcSMA5Minute() {
    sma_1_min
        .groupBy(Pair::getKey)
        .subscribe(
            k ->
                k.buffer(slot, 1)
                    .subscribe(
                        l -> {
                          Double d =
                              l.stream().mapToDouble(e -> e.getValue()).average().getAsDouble();
                          sma_5_min.onNext(
                              new Pair<String, Double>(l.get(0).getKey(), formatDouble(d)));
                          String stockName = l.get(0).getKey();
                          Stock stock =
                              repository.findByStockName(stockName) != null
                                  ? repository.findByStockName(stockName)
                                  : new Stock();
                          stock.setStockName(stockName);
                          stock.getFiveMinuteAverage().put(getTimeRange(), formatDouble(d));
                          repository.save(stock);
                        }));
  }

  private void calcSMAFast(Subject<Pair<String, Double>> sma_5) {
    sma_5
        .groupBy(Pair::getKey)
        .subscribe(
            k ->
                k.buffer(fast * slot, 1)
                    .subscribe(
                        l -> {
                          Double d =
                              l.stream().mapToDouble(e -> e.getValue()).average().getAsDouble();
                          sma_fast.onNext(
                              new Pair<String, Double>(l.get(0).getKey(), formatDouble(d)));
                          String stockName = l.get(0).getKey();
                          Stock stock =
                              repository.findByStockName(stockName) != null
                                  ? repository.findByStockName(stockName)
                                  : new Stock();
                          stock.setStockName(stockName);
                          stock.getFastMovingAverage().put(getTimeRange(), formatDouble(d));
                          repository.save(stock);
                        }));
  }

  private void calcSMAFastPair() {
    sma_fast
        .groupBy(Pair::getKey)
        .subscribe(
            k ->
                k.buffer(6, 1)
                    .subscribe(
                        l -> {
                          stockSMAFastPair
                              .computeIfAbsent(
                                  k.getKey(), n -> BehaviorSubject.<Pair<Double, Double>>create())
                              .onNext(new Pair<>(l.get(0).getValue(), l.get(5).getValue()));
                        }));
  }

  private void calcSMAMinute(Subject<Pair<String, String>> stockStream) {
    stockStream
        .groupBy(Pair::getKey)
        .subscribe(
            k ->
                k.buffer(1, TimeUnit.MINUTES)
                    .filter(l -> !l.isEmpty())
                    .subscribe(
                        l -> {
                          Double d =
                              l.stream()
                                  .mapToDouble(e -> Double.parseDouble(e.getValue()))
                                  .average()
                                  .getAsDouble();
                          sma_1_min.onNext(new Pair<String, Double>(l.get(0).getKey(), d));
                          String stockName = l.get(0).getKey();
                          Stock stock =
                              repository.findByStockName(stockName) != null
                                  ? repository.findByStockName(stockName)
                                  : new Stock();
                          stock.setStockName(stockName);
                          stock.getMinuteAverage().put(getTimeRange(), formatDouble(d));
                          repository.save(stock);
                        }));
  }

  private void calcSMASlow(Subject<Pair<String, Double>> sma_5) {
    sma_5
        .groupBy(Pair::getKey)
        .subscribe(
            k ->
                k.buffer(slow * slot, 1)
                    .subscribe(
                        l -> {
                          Double d =
                              l.stream().mapToDouble(e -> e.getValue()).average().getAsDouble();
                          sma_slow.onNext(
                              new Pair<String, Double>(l.get(0).getKey(), formatDouble(d)));
                          String stockName = l.get(0).getKey();
                          Stock stock =
                              repository.findByStockName(stockName) != null
                                  ? repository.findByStockName(stockName)
                                  : new Stock();
                          stock.setStockName(stockName);
                          stock.getSlowMovingAverage().put(getTimeRange(), formatDouble(d));
                          repository.save(stock);
                        }));
  }

  private void calcSMASlowPair() {
    sma_slow
        .groupBy(Pair::getKey)
        .subscribe(
            k ->
                k.buffer(6, 1)
                    .subscribe(
                        l -> {
                          stockSMASlowPair
                              .computeIfAbsent(
                                  k.getKey(), n -> BehaviorSubject.<Pair<Double, Double>>create())
                              .onNext(new Pair<>(l.get(0).getValue(), l.get(5).getValue()));
                        }));
  }

  private String convertToRange(int number) {
    return "" + (number < 5 ? 0 : number / 5 == 0 ? number : (number / 5) * 5);
  }

  private Double formatDouble(Double d) {
    return Math.round(d * 10.0) / 10.0;
  }

  public Subject<Pair<String, Double>> getSma_1_min() {
    return sma_1_min;
  }

  public Subject<Pair<String, Double>> getSma_5_min() {
    return sma_5_min;
  }

  public Map<String, Subject<Pair<Double, Double>>> getStockSMAFastPair() {
    return stockSMAFastPair;
  }

  public Map<String, Subject<Pair<Double, Double>>> getStockSMASlowPair() {
    return stockSMASlowPair;
  }

  private String getTimeRange() {
    return LocalDate.now().getDayOfYear()
        + ":"
        + LocalTime.now().getHour()
        + ":"
        + LocalTime.now().getMinute();
  }

  public void startCalculation(Subject<Pair<String, String>> stockStream) {
    calcSMAMinute(stockStream);
    calcSMA5Minute();
    calcSMASlow(sma_5_min);
    calcSMAFast(sma_5_min);
    calcSMASlowPair();
    calcSMAFastPair();
  }

  public void startInitCalculation(Subject<Pair<String, Double>> sma_5) {
    calcSMASlow(sma_5);
    calcSMAFast(sma_5);
    calcSMASlowPair();
    calcSMAFastPair();
  }
}
