import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import io.reactivex.subjects.Subject;

public class DecisionMaker {

  private static DecisionMaker decisionMaker;
  private final Map<String, String> position;

  private DecisionMaker() {
    position = new HashMap<>();
  }

  public static final DecisionMaker getInstance() {
    if (decisionMaker == null) decisionMaker = new DecisionMaker();
    return decisionMaker;
  }

  public void startTakingDecision() throws Exception {
    Set<Map.Entry<String, Subject<Pair<Double, Double>>>> smaSlowList =
        SMACalculator.getInstance().getStockSMASlowPair().entrySet();
    Set<Map.Entry<String, Subject<Pair<Double, Double>>>> smaFastList =
        SMACalculator.getInstance().getStockSMAFastPair().entrySet();
    new Thread(
            () -> {
              while (true) {
                if (smaSlowList.isEmpty())
                  try {
                    Thread.sleep(1000 * 60);
                  } catch (InterruptedException e) {
                    e.printStackTrace();
                  }
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
                                        && !position.getOrDefault(k.getKey(), "").equals("BUY"))
                                      ApplicationController.getInstance()
                                          .getQuery()
                                          .add(
                                              new StockMessage(
                                                  Integer.MAX_VALUE,
                                                  "BUY",
                                                  new Pair<String, String>(
                                                      k.getKey(),
                                                      l.getValue().getValue().toString())));
                                    else if (getResult(l.getKey(), l.getValue()) == -1
                                        && !position.getOrDefault(k.getKey(), "").equals("SELL"))
                                      ApplicationController.getInstance()
                                          .getQuery()
                                          .add(
                                              new StockMessage(
                                                  Integer.MAX_VALUE,
                                                  "SELL",
                                                  new Pair<String, String>(
                                                      k.getKey(),
                                                      l.getValue().getValue().toString())));
                                  }))
                  .forEach(System.out::print);
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
        : (int) Math.signum(smaFast0 - smaSlow0);
  }

  public Map<String, String> getPosition() {
    return position;
  }
}
