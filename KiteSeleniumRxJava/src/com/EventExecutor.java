package com;

import java.util.List;
import java.util.concurrent.PriorityBlockingQueue;

import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.Subject;

public class EventExecutor {
  private static EventExecutor eventExecutor;
  private final PriorityBlockingQueue<StockMessage> queue;
  private final Subject<Pair<String, String>> result;

  private static final double target = 0.01;
  private static final double stopLoss = 0.005;

  private EventExecutor() {
    queue = new PriorityBlockingQueue<>(10, new StockMessageComparator());
    result = BehaviorSubject.create();
  }

  public static final EventExecutor getInstance() {
    if (eventExecutor == null) eventExecutor = new EventExecutor();
    return eventExecutor;
  }

  public void startExecution() {
    EventExecutor event = EventExecutor.getInstance();

    new Thread(
            () -> {
              SMACalculator.getInstance().startCalculation(event.result);
              try {
                DecisionMaker.getInstance().startTakingDecision();
              } catch (Exception e) {
                e.printStackTrace();
              }
              while (true) {
                try {
                  StockMessage message = event.queue.take();
                  System.out.println(event.queue.size() + "  =  " + message.getMessage());

                  switch (message.getMessage()) {
                    case "ClickMarketWatch":
                      WebAction.getInstance().clickMarketWatch(message.getPair().getValue());
                      List<Pair<String, String>> data = WebAction.getInstance().readStockPrice();
                      data.forEach(k -> event.result.onNext(k));
                      break;
                    case "ReadStockValue":
                      WebAction.getInstance().readStockPrice().forEach(k -> event.result.onNext(k));
                      break;
                    case "BUY":
                      String buyPrice = message.getPair().getValue();
                      String buyName = message.getPair().getKey();
                      String i = StockLocation.getPosition(buyName);
                      String buyTarget = event.getTarget(buyPrice);
                      String buyStopLoss = event.getStopLoss(buyPrice);
                      WebAction.getInstance()
                          .clickMarketWatch(StockLocation.getMarketWatch(buyName));
                      WebAction.getInstance()
                          .buySellBO(i, "1", buyPrice, buyTarget, buyStopLoss, "1", true);
                      DecisionMaker.getInstance().getPosition().put(buyName, "BUY");
                      break;
                    case "SELL":
                      String sellPrice = message.getPair().getValue();
                      String sellName = message.getPair().getKey();
                      String j = StockLocation.getPosition(sellName);
                      String sellTarget = event.getTarget(sellPrice);
                      String sellStopLoss = event.getStopLoss(sellPrice);
                      WebAction.getInstance()
                          .clickMarketWatch(StockLocation.getMarketWatch(sellName));
                      WebAction.getInstance()
                          .buySellBO(j, "1", sellPrice, sellTarget, sellStopLoss, "1", false);
                      DecisionMaker.getInstance().getPosition().put(sellName, "SELL");
                      break;
                  }
                } catch (Exception e) {
                  e.printStackTrace();
                }
                // WebAction.sleep(100);
              }
            })
        .start();
  }

  private String getTarget(String price) {
    return "" + Double.parseDouble(price) * target;
  }

  private String getStopLoss(String price) {
    return "" + Double.parseDouble(price) * stopLoss;
  }

  public PriorityBlockingQueue<StockMessage> getQueue() {
    return queue;
  }

  public Subject<Pair<String, String>> getResult() {
    return result;
  }
}
