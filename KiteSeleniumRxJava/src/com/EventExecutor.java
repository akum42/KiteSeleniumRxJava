package com;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.PriorityBlockingQueue;

import org.springframework.stereotype.Service;

import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.Subject;

@Service
public class EventExecutor {

  private final PriorityBlockingQueue<StockMessage> queue;
  private final Subject<Pair<String, String>> result;
  private final Map<String, String> position;

  private static final double target = 0.005;
  private static final double stopLoss = 0.0025;

  public EventExecutor() {
    queue = new PriorityBlockingQueue<>(10, new StockMessageComparator());
    result = BehaviorSubject.create();
    position = new HashMap<>();
  }

  public void startExecution() {

    new Thread(
            () -> {
              while (true) {
                try {
                  StockMessage message = this.queue.take();
                  System.out.println(
                      this.queue.size()
                          + " "
                          + message.getMessage()
                          + " "
                          + message.getPair().getKey());

                  switch (message.getMessage()) {
                    case "ClickMarketWatch":
                      WebAction.getInstance().clickMarketWatch(message.getPair().getValue());
                      List<Pair<String, String>> data = WebAction.getInstance().readStockPrice();
                      data.forEach(k -> this.result.onNext(k));
                      break;
                    case "ReadStockValue":
                      WebAction.getInstance().readStockPrice().forEach(k -> result.onNext(k));
                      break;
                    case "BUY":
                      String buyPrice = message.getPair().getValue();
                      String buyName = message.getPair().getKey();
                      String i = StockLocation.getPosition(buyName);
                      String buyTarget = this.getTarget(buyPrice);
                      String buyStopLoss = this.getStopLoss(buyPrice);
                      WebAction.getInstance()
                          .clickMarketWatch(StockLocation.getMarketWatch(buyName));
                      WebAction.getInstance()
                          .buySellBO(i, "1", buyPrice, buyTarget, buyStopLoss, "1", true);
                      getPosition().put(buyName, "BUY");
                      break;
                    case "SELL":
                      String sellPrice = message.getPair().getValue();
                      String sellName = message.getPair().getKey();
                      String j = StockLocation.getPosition(sellName);
                      String sellTarget = getTarget(sellPrice);
                      String sellStopLoss = getStopLoss(sellPrice);
                      WebAction.getInstance()
                          .clickMarketWatch(StockLocation.getMarketWatch(sellName));
                      WebAction.getInstance()
                          .buySellBO(j, "1", sellPrice, sellTarget, sellStopLoss, "1", false);
                      getPosition().put(sellName, "SELL");
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
    return "" + Double.parseDouble(price) * target*100/100;
  }

  private String getStopLoss(String price) {
    return "" + Double.parseDouble(price) * stopLoss*100/100;
  }

  public PriorityBlockingQueue<StockMessage> getQueue() {
    return queue;
  }

  public Subject<Pair<String, String>> getResult() {
    return result;
  }

  public Map<String, String> getPosition() {
    return position;
  }
}
