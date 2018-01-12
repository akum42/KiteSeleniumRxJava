package com;

import static com.Util.sleep;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CleanOrders {
  @Autowired private EventExecutor eventExecutor;

  public void clearAllOrders() throws Exception {

    new Thread(
            () -> {
              eventExecutor
                  .getQueue()
                  .add(new StockMessage(Integer.MAX_VALUE, "ClearAllOrders", null));
            })
        .start();
  }

  public void clearOldOrders() throws Exception {

    new Thread(
            () -> {
              while (true) {
                sleep(1000 * 60 * 5);
                eventExecutor
                    .getQueue()
                    .add(new StockMessage(Integer.MAX_VALUE, "ClearOldOrders", null));
              }
            })
        .start();
  }
}
