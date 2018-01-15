package com;

import static com.Util.canOrder;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.PriorityBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.Subject;

@Service
public class EventExecutor {

  private static final double target = 0.0068;

  private static final double stopLoss = 0.0031;
  @Autowired private WebAction webAction;
  private final PriorityBlockingQueue<StockMessage> queue;
  private final Subject<Pair<String, String>> result;

  private final Map<String, String> position;
  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  public EventExecutor() {
    queue = new PriorityBlockingQueue<>(10, Comparator.comparing(StockMessage::getPriority));
    result = BehaviorSubject.create();
    position = new HashMap<>();
  }

  public void clearQueue() {
    queue.clear();
  }

  public Map<String, String> getPosition() {
    return position;
  }

  public PriorityBlockingQueue<StockMessage> getQueue() {
    return queue;
  }

  public Subject<Pair<String, String>> getResult() {
    return result;
  }

  private String getStopLoss(String price) {
    return "" + Math.round(Double.parseDouble(price) * stopLoss * 10.0) / 10.0;
  }

  private String getTarget(String price) {
    return "" + Math.round(Double.parseDouble(price) * target * 10.0) / 10.0;
  }
  
  private String getQuantity(String price) {
	  return "" + (int)(2000/Double.parseDouble(price));
  }
  
  public void startExecution() {

    new Thread(
            () -> {
              while (true) {
                try {
                  StockMessage message = this.queue.take();

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
                      webAction.clickMarketWatch(StockLocation.getMarketWatch(buyName));
                      getPosition().put(buyName, "BUY");
                      if (Double.parseDouble(buyPrice) < 2000d && Double.parseDouble(buyPrice) >50d && canOrder())
                        webAction.buySellBO(i, getQuantity(buyPrice), buyPrice, getTarget(buyPrice), getStopLoss(buyPrice), "1", true);
                      logger.info("BUY " + buyName + " " + buyPrice);
                      break;
                    case "SELL":
                      String sellPrice = message.getPair().getValue();
                      String sellName = message.getPair().getKey();
                      String j = StockLocation.getPosition(sellName);
                      webAction.clickMarketWatch(StockLocation.getMarketWatch(sellName));
                      getPosition().put(sellName, "SELL");
                      if (Double.parseDouble(sellPrice) < 2000d && Double.parseDouble(sellPrice) >50d && canOrder())
                        webAction.buySellBO(
                            j, getQuantity(sellPrice), sellPrice,  getTarget(sellPrice), getStopLoss(sellPrice), "1", false);
                      logger.info("SELL " + sellName + " " + sellPrice);
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
                  logger.error(e.getMessage());
                }
              }
            })
        .start();
  }
}
