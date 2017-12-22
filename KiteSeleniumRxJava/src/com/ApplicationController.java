package com;

import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;

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

  public static void main(String[] args) throws Exception {
    SpringApplication.run(ApplicationController.class, args);
  }

  @Override
  public void run(String... args) throws Exception {
    //Runtime.getRuntime().exec("cmd /c start \"\" D:\\test\\startMongo.bat");

    //WebAction.sleep(1000 * 60);

    loadData();

    if (isMarketOpen() == 1) {
      WebAction.getInstance().login(args[0], args[1], args[2], args[3]);

      new Thread(
              () -> {
                while (true) {
                  for (int i = 2; i <= 6; i++) {
                    eventExecutor
                        .getQueue()
                        .add(
                            new StockMessage(
                                0, "ClickMarketWatch", new Pair<String, String>("", "" + i)));
                    WebAction.sleep(3000);
                  }
                }
              })
          .start();

      eventExecutor.startExecution();
      smaCalculator.startCalculation(eventExecutor.getResult());
      decisionMaker.startTakingDecision(
          smaCalculator.getStockSMASlowPair().entrySet(),
          smaCalculator.getStockSMAFastPair().entrySet());

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

  private void loadData() {
    List<Stock> stockList = repository.findAll();
    Subject<Pair<String, Double>> sma_5 = smaCalculator.getSma_5_min();
    smaCalculator.startInitCalculation(sma_5);
    stockList.forEach(
        k ->
            k.getFiveMinuteAverage()
                .stream()
                .skip(k.getFiveMinuteAverage().size() - 30)
                .forEach(
                    l -> sma_5.onNext(new Pair<String, Double>(k.getStockName(), l.getValue()))));
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
