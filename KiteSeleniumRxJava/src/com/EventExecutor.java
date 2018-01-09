package com;

import static com.Util.orderPlaceTill;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.PriorityBlockingQueue;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.Subject;

@Service
public class EventExecutor {

  @Autowired private WebAction webAction;

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
                  /*
                   * System.out.println( this.queue.size() + " " + message.getMessage() + " " +
                   * message.getPair().getKey());
                   */

                  switch (message.getMessage()) {
                    case "ClickMarketWatch":
                      webAction.clickMarketWatch(message.getPair().getValue());
                      List<Pair<String, String>> data = webAction.readStockPrice();
                      data.forEach(k -> this.result.onNext(k));
                      break;
                    case "ReadStockValue":
                      webAction.readStockPrice().forEach(k -> result.onNext(k));
                      break;
                    case "BUY":
                      String buyPrice = message.getPair().getValue();
                      String buyName = message.getPair().getKey();
                      String i = StockLocation.getPosition(buyName);
                      String buyTarget = this.getTarget(buyPrice);
                      String buyStopLoss = this.getStopLoss(buyPrice);
                      webAction.clickMarketWatch(StockLocation.getMarketWatch(buyName));
                      if (Double.parseDouble(buyPrice) < 2000d && orderPlaceTill() > 0)
                        webAction.buySellBO(i, "1", buyPrice, buyTarget, buyStopLoss, "1", true);
                      System.out.println("BUY " + buyName + " " + buyPrice);
                      getPosition().put(buyName, "BUY");
                      break;
                    case "SELL":
                      String sellPrice = message.getPair().getValue();
                      String sellName = message.getPair().getKey();
                      String j = StockLocation.getPosition(sellName);
                      String sellTarget = getTarget(sellPrice);
                      String sellStopLoss = getStopLoss(sellPrice);
                      webAction.clickMarketWatch(StockLocation.getMarketWatch(sellName));
                      if (Double.parseDouble(sellPrice) < 2000d && orderPlaceTill() > 0)
                        webAction.buySellBO(
                            j, "1", sellPrice, sellTarget, sellStopLoss, "1", false);
                      System.out.println("SELL " + sellName + " " + sellPrice);
                      getPosition().put(sellName, "SELL");
                      break;
                    case "ClearOldOrders":
                      String orderNumber = webAction.getAllOpenOrders();
                      List<String> orderTime = webAction.getOrderTime(orderNumber);
                      webAction.exitOlderOrder(orderTime);
                      break;
                    case "ClearAllOrders":
                      webAction.exitAllOpenOrder();
                      break;
                  }
                } catch (Exception e) {
                 System.out.println(e.getMessage() ); 
                }
              }
            })
        .start();
  }

  private String getTarget(String price) {
    return "" + Math.round(Double.parseDouble(price) * target * 10.0) / 10.0;
  }

  private String getStopLoss(String price) {
    return "" + Math.round(Double.parseDouble(price) * stopLoss * 10.0) / 10.0;
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
