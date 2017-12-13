package com;

import java.time.LocalTime;
import java.util.Comparator;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ApplicationController implements CommandLineRunner {

  public static void main(String[] args) throws Exception {
    SpringApplication.run(ApplicationController.class, args);
  }

  @Override
  public void run(String... args) throws Exception {

    if (isMarketOpen() == 1) {
      WebAction.getInstance().login(args[0], args[1], args[2], args[3]);

      new Thread(
              () -> {
                while (true) {
                  for (int i = 2; i <= 6; i++) {
                    EventExecutor.getInstance()
                        .getQueue()
                        .add(
                            new StockMessage(
                                0, "ClickMarketWatch", new Pair<String, String>("", "" + i)));
                    WebAction.sleep(3000);
                  }
                }
              })
          .start();

      EventExecutor.getInstance().startExecution();

      new Thread(
              () -> {
                while (true) {
                  WebAction.getInstance().readPostion();
                  WebAction.sleep(1000 * 60 * 30);
                }
              })
          .start();
    }
    while (true)
      if (isMarketOpen() == 1) WebAction.sleep(1000 * 60 * 5);
      else {
        WebAction.getInstance().logout();
        System.exit(0);
      }
  }

  private static int isMarketOpen() {
    return LocalTime.of(15, 15).compareTo(LocalTime.now())
        * LocalTime.now().compareTo(LocalTime.of(9, 15));
  }
}

class StockMessageComparator implements Comparator<StockMessage> {

  @Override
  public int compare(StockMessage o1, StockMessage o2) {
    return o1.getPriority() - o2.getPriority();
  }
}
