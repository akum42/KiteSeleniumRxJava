package com;

public class StockMessage {
  private final int priority;
  private final String message;
  private final Pair<String, String> pair;

  public StockMessage(int priority, String message, Pair<String, String> pair) {
    super();
    this.priority = priority;
    this.message = message;
    this.pair = pair;
  }

  public int getPriority() {
    return priority;
  }

  public Pair<String, String> getPair() {
    return pair;
  }

  public String getMessage() {
    return message;
  }
}
