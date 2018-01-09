package com;

import static com.Util.marketOpenTill;
import static com.Util.orderPlaceTill;
import static com.Util.sleep;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.reactivex.subjects.Subject;

@SpringBootApplication
public class ApplicationController implements CommandLineRunner {

  @Autowired private StockRepository repository;
  @Autowired private EventExecutor eventExecutor;
  @Autowired private SMACalculator smaCalculator;
  @Autowired private DecisionMaker decisionMaker;
  @Autowired private CleanOrders cleanOrders;
  @Autowired private WebAction webAction;
  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  public static void main(String[] args) throws Exception {
    SpringApplication.run(ApplicationController.class, args);
  }

  @Override
  public void run(String... args) throws Exception {
    loadData();

    if (orderPlaceTill() == 1) {
      webAction.login(args[0], args[1], args[2], args[3]);

      new Thread(
              () -> {
                while (true) {
                  for (int i = 2; i <= 6; i++) {
                    eventExecutor
                        .getQueue()
                        .add(
                            new StockMessage(
                                0, "ClickMarketWatch", new Pair<String, String>("", "" + i)));
                    sleep(3000);
                  }
                }
              })
          .start();

      eventExecutor.startExecution();
      smaCalculator.startCalculation(eventExecutor.getResult());
      System.out.println(Boolean.parseBoolean(args[4]));
      if (Boolean.parseBoolean(args[4])) {
        decisionMaker.startTakingDecision(
            smaCalculator.getStockSMASlowPair().entrySet(),
            smaCalculator.getStockSMAFastPair().entrySet());
        cleanOrders.clearOldOrders();
      }

      new Thread(
              () -> {
                webAction.readPostion();
              })
          .start();
    }
    while (true) {
      if (orderPlaceTill() > 0) sleep(1000 * 60 * 2);
      else {
        logger.info("Clear All Orders");
        cleanOrders.clearAllOrders();
      }
      if (marketOpenTill() < 0) {
        webAction.logout();
        System.exit(0);
      }
    }
  }

  private void clearOldData() {
    List<Stock> stockList = repository.findAll();
    System.out.println(
        stockList
            .stream()
            .map(k -> k.getMinuteAverage())
            .flatMap(k -> k.entrySet().stream())
            .skip(10)
            .collect(Collectors.toConcurrentMap(p -> p.toString(), p -> p.toString())));
  }

  private void loadData() {
    List<Stock> stockList = repository.findAll();
    Subject<Pair<String, Double>> sma_5 = smaCalculator.getSma_5_min();
    smaCalculator.startInitCalculation(sma_5);
    stockList.forEach(
        k ->
            k.getFiveMinuteAverage()
                .stream()
                .skip(
                    k.getFiveMinuteAverage().size() - 32 * 5 > 0
                        ? k.getFiveMinuteAverage().size() - 32 * 5
                        : k.getFiveMinuteAverage().size())
                .forEach(
                    l -> sma_5.onNext(new Pair<String, Double>(k.getStockName(), l.getValue()))));
  }
}

class StockMessageComparator implements Comparator<StockMessage> {

  @Override
  public int compare(StockMessage o1, StockMessage o2) {
    return o1.getPriority() - o2.getPriority();
  }
}
