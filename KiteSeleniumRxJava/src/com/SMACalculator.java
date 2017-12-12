package com;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.Subject;

public class SMACalculator {
  public static SMACalculator smaCalculator;
  private final Subject<Pair<String, Double>> sma_1_min;
  private final Subject<Pair<String, Double>> sma_5_min;
  private final Subject<Pair<String, Double>> sma_slow;
  private final Subject<Pair<String, Double>> sma_fast;
  private final Map<String, Subject<Pair<Double, Double>>> stockSMASlowPair;
  private final Map<String, Subject<Pair<Double, Double>>> stockSMAFastPair;

  private final int slow = 3;
  private final int fast = 1;

  private SMACalculator() {
    sma_1_min = BehaviorSubject.create();
    sma_5_min = BehaviorSubject.create();
    sma_slow = BehaviorSubject.create();
    sma_fast = BehaviorSubject.create();
    stockSMASlowPair = new ConcurrentHashMap<>();
    stockSMAFastPair = new ConcurrentHashMap<>();
  }

  public static final SMACalculator getInstance() {
    if (smaCalculator == null) smaCalculator = new SMACalculator();
    return smaCalculator;
  }

  public void startCalculation(Subject<Pair<String, String>> stockStream) {
    calcSMAMinute(stockStream);
    calcSMA5Minute();
    calcSMASlow();
    calcSMAFast();
    calcSMASlowPair();
    calcSMAFastPair();
  }

  private void calcSMAFastPair() {
    sma_fast
        .groupBy(Pair::getKey)
        .subscribe(
            k ->
                k.buffer(2, 1)
                    .subscribe(
                        l -> {
                          stockSMAFastPair
                              .computeIfAbsent(
                                  k.getKey(), n -> BehaviorSubject.<Pair<Double, Double>>create())
                              .onNext(new Pair<>(l.get(0).getValue(), l.get(1).getValue()));
                        }));
  }

  private void calcSMASlowPair() {
    sma_slow
        .groupBy(Pair::getKey)
        .subscribe(
            k ->
                k.buffer(2, 1)
                    .subscribe(
                        l -> {
                          stockSMASlowPair
                              .computeIfAbsent(
                                  k.getKey(), n -> BehaviorSubject.<Pair<Double, Double>>create())
                              .onNext(new Pair<>(l.get(0).getValue(), l.get(1).getValue()));
                        }));
  }

  private void calcSMAFast() {
    sma_5_min
        .groupBy(Pair::getKey)
        .subscribe(
            k ->
                k.buffer(fast, 1)
                    .subscribe(
                        l -> {
                          Double d =
                              l.stream().mapToDouble(e -> e.getValue()).average().getAsDouble();
                          sma_fast.onNext(new Pair<String, Double>(l.get(0).getKey(), d));
                        }));
  }

  private void calcSMASlow() {
    sma_5_min
        .groupBy(Pair::getKey)
        .subscribe(
            k ->
                k.buffer(slow, 1)
                    .subscribe(
                        l -> {
                          Double d =
                              l.stream().mapToDouble(e -> e.getValue()).average().getAsDouble();
                          sma_slow.onNext(new Pair<String, Double>(l.get(0).getKey(), d));
                        }));
  }

  private void calcSMA5Minute() {
    sma_1_min
        .groupBy(Pair::getKey)
        .subscribe(
            k ->
                k.buffer(1)
                    .subscribe(
                        l -> {
                          Double d =
                              l.stream().mapToDouble(e -> e.getValue()).average().getAsDouble();
                          sma_5_min.onNext(new Pair<String, Double>(l.get(0).getKey(), d));
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
                        }));
  }

  public Map<String, Subject<Pair<Double, Double>>> getStockSMASlowPair() {
    return stockSMASlowPair;
  }

  public Map<String, Subject<Pair<Double, Double>>> getStockSMAFastPair() {
    return stockSMAFastPair;
  }
}
