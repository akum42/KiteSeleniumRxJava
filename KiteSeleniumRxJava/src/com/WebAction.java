package com;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class WebAction {

  private static WebAction webAction;
  private WebDriver driver;

  private WebAction() {
    System.setProperty("webdriver.chrome.driver", "E:\\chromedriver.exe");
    driver = new ChromeDriver();
    String baseUrl = "https://kite.zerodha.com/";
    driver.get(baseUrl);
  }

  public static final WebAction getInstance() {
    if (webAction == null) webAction = new WebAction();
    return webAction;
  }

  public void logout() {
    focusOnElement("header#header nav > a");
    clickfocusOnElement("header#header li:nth-child(10)");
    driver.close();
    System.exit(0);
  }

  public void clickMarketWatch(String i) {
    findElementByCSS("ul#marketwatches li:nth-child(" + i + ") > a").click();
  }

  public List<Pair<String, String>> readStockPrice() {

    return IntStream.rangeClosed(1, 20)
        .mapToObj(
            j -> {
              String stockName =
                  findElementByCSS(
                          "ul#instruments li:nth-child(" + j + ") > div > div.symbol.ng-binding")
                      .getText();
              String stockPrice =
                  findElementByCSS(
                          "ul#instruments li:nth-child("
                              + j
                              + ") > div > div.price-block.text-right > span.price > span.ng-binding.ng-scope")
                      .getText();
              return new Pair<String, String>(stockName, stockPrice);
            })
        .collect(Collectors.toList());
  }

  public void buySellBO(
      String i,
      String quantity,
      String price,
      String stopLoss,
      String target,
      String trailingStopLoss,
      boolean isBuy) {
    focusOnElement("ul#instruments li:nth-child(" + i + ") > div");
    if (isBuy)
      clickfocusOnElement(
          "ul#instruments li:nth-child("
              + i
              + ") > div > span > button.button.button-clear.buy.ng-scope.ng-isolate-scope.hint--top.hint--rounded");
    else
      clickfocusOnElement(
          "ul#instruments li:nth-child("
              + i
              + ") > div > span > button.button.button-clear.sell.ng-scope.ng-isolate-scope.hint--top.hint--rounded");

    sleep(50);

    findElementByCSS("form#buysellform div.show-advanced-options").click();
    findElementByCSS("form#buysellform div.varities-block > label:nth-child(2)").click();

    findElementByCSS("input#quantity").clear();
    findElementByCSS("input#quantity").sendKeys(quantity);
    findElementByCSS("input#price").sendKeys(price);

    findElementByCSS("input#stoploss").sendKeys(stopLoss);
    findElementByCSS("input#squareoff").sendKeys(target);
    findElementByCSS("input#trailingstoploss").sendKeys(trailingStopLoss);

    findElementByCSS("form#buysellform button[type=\"submit\"]").click();
  }

  public void buySellMISMarket(String i, String quantity, boolean isBuy) {
    focusOnElement("ul#instruments li:nth-child(" + i + ") > div");
    if (isBuy)
      clickfocusOnElement(
          "ul#instruments li:nth-child("
              + i
              + ") > div > span > button.button.button-clear.buy.ng-scope.ng-isolate-scope.hint--top.hint--rounded");
    else
      clickfocusOnElement(
          "ul#instruments li:nth-child("
              + i
              + ") > div > span > button.button.button-clear.sell.ng-scope.ng-isolate-scope.hint--top.hint--rounded");

    sleep(50);

    findElementByCSS("form#buysellform div.five.columns.product > label:nth-child(2)").click();
    findElementByCSS("form#buysellform div.seven.columns.text-right > label:nth-child(1)").click();

    findElementByCSS("input#quantity").clear();
    findElementByCSS("input#quantity").sendKeys(quantity);

    findElementByCSS("form#buysellform button[type=\"submit\"]").click();
  }

  public void buySellMISLimit(String i, String quantity, String price, boolean isBuy) {
    focusOnElement("ul#instruments li:nth-child(" + i + ") > div");
    if (isBuy)
      clickfocusOnElement(
          "ul#instruments li:nth-child("
              + i
              + ") > div > span > button.button.button-clear.buy.ng-scope.ng-isolate-scope.hint--top.hint--rounded");
    else
      clickfocusOnElement(
          "ul#instruments li:nth-child("
              + i
              + ") > div > span > button.button.button-clear.sell.ng-scope.ng-isolate-scope.hint--top.hint--rounded");

    sleep(50);

    findElementByCSS("form#buysellform div.five.columns.product > label:nth-child(2)").click();
    findElementByCSS("form#buysellform div.seven.columns.text-right > label:nth-child(2)").click();

    findElementByCSS("input#quantity").clear();
    findElementByCSS("input#quantity").sendKeys(quantity);
    findElementByCSS("input#price").sendKeys(price);

    findElementByCSS("form#buysellform button[type=\"submit\"]").click();
  }

  public void login(String userName, String passWord, String p1, String p2) {
    findElementByCSS("input[name=\"user_id\"]").sendKeys(userName);
    findElementByCSS("input#inputtwo").sendKeys(passWord);
    findElementByCSS("form#loginform button[type=\"submit\"]").click();

    findElementByCSS("input[name=\"answer1\"]").sendKeys(p1);
    findElementByCSS("input[name=\"answer2\"]").sendKeys(p2);
    findElementByCSS("form#twofaform button[type=\"submit\"]").click();
  }

  public void readPostion() {
    findElementByCSS("header#header div > nav > ul > li:nth-child(4) > a").click();
    //TODO read position
  }

  private final WebElement findElementByCSS(String element) {
    return (new WebDriverWait(driver, 10))
        .until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(element)));
  }

  private final void focusOnElement(String element) {
    new Actions(driver).moveToElement(findElementByCSS(element)).perform();
  }

  private final void clickfocusOnElement(String element) {
    ((JavascriptExecutor) driver)
        .executeScript("document.querySelector('" + element + "').click();");
  }

  public static final void sleep(long millis) {
    try {
      Thread.sleep(millis);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
}
