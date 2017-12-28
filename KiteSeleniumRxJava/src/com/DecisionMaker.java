package com;

import static com.Util.sleep;

import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.reactivex.subjects.Subject;

@Service
public class DecisionMaker {
  @Autowired private EventExecutor eventExecutor;

  public void startTakingDecision(
      Set<Map.Entry<String, Subject<Pair<Double, Double>>>> smaSlowList,
      Set<Map.Entry<String, Subject<Pair<Double, Double>>>> smaFastList)
      throws Exception {

    new Thread(
            () -> {
              while (true) {
                if (smaSlowList.isEmpty()) sleep(1000 * 60);
                else break;
              }
              smaSlowList
                  .parallelStream()
                  .map(
                      k ->
                          k.getValue()
                              .zipWith(
                                  smaFastList
                                      .parallelStream()
                                      .filter(l -> l.getKey().equals(k.getKey()))
                                      .map(y -> y.getValue())
                                      .findFirst()
                                      .get(),
                                  (i1, i2) ->
                                      new Pair<Pair<Double, Double>, Pair<Double, Double>>(i1, i2))
                              .subscribe(
                                  l -> {
                                    if (getResult(l.getKey(), l.getValue()) == 1
                                        && !eventExecutor
                                            .getPosition()
                                            .getOrDefault(k.getKey(), "")
                                            .equals("BUY"))
                                      eventExecutor
                                          .getQueue()
                                          .add(
                                              new StockMessage(
                                                  Integer.MAX_VALUE,
                                                  "BUY",
                                                  new Pair<String, String>(
                                                      k.getKey(),
                                                      l.getValue().getValue().toString())));
                                    else if (getResult(l.getKey(), l.getValue()) == -1
                                        && !eventExecutor
                                            .getPosition()
                                            .getOrDefault(k.getKey(), "")
                                            .equals("SELL"))
                                      eventExecutor
                                          .getQueue()
                                          .add(
                                              new StockMessage(
                                                  Integer.MAX_VALUE,
                                                  "SELL",
                                                  new Pair<String, String>(
                                                      k.getKey(),
                                                      l.getValue().getValue().toString())));
                                  }))
                  .forEach(System.err::print);
            })
        .start();
  }

  private int getResult(Pair<Double, Double> smaSlow, Pair<Double, Double> smaFast) {
    Double smaSlow0 = smaSlow.getKey();
    Double smaSlow1 = smaSlow.getValue();
    Double smaFast0 = smaFast.getKey();
    Double smaFast1 = smaFast.getValue();
    return ((int) Math.signum(smaSlow0 - smaFast0) == (int) Math.signum(smaSlow1 - smaFast1))
        ? 0
        : (int) Math.signum(smaFast1 - smaSlow1);
  }
}
