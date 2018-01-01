package com;

import static com.Util.sleep;
import static java.time.temporal.ChronoUnit.MINUTES;

import java.time.LocalTime;
import java.util.ArrayList;
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
		if (webAction == null)
			webAction = new WebAction();
		return webAction;
	}

	public void logout() {
		focusOnElement("header#header nav > a");
		clickfocusOnElement("header#header li:nth-child(10)");
		driver.close();
		System.exit(0);
	}

	public void clickMarketWatch(String i) {
		clickElement("ul#marketwatches li:nth-child(" + i + ") > a");
		sleep(100);
	}

	public List<Pair<String, String>> readStockPrice() {

		return IntStream.rangeClosed(1, 20).mapToObj(j -> {
			String stockName = findElementByCSS("ul#instruments li:nth-child(" + j + ") > div > div.symbol.ng-binding")
					.getText();
			String stockPrice = findElementByCSS("ul#instruments li:nth-child(" + j
					+ ") > div > div.price-block.text-right > span.price > span.ng-binding.ng-scope").getText();
			return new Pair<String, String>(stockName, stockPrice);
		}).collect(Collectors.toList());
	}

	public void buySellBO(String i, String quantity, String price, String target, String stopLoss,
			String trailingStopLoss, boolean isBuy) {
		focusOnElement("ul#instruments li:nth-child(" + i + ") > div");
		sleep(50);
		if (isBuy)
			clickfocusOnElement("ul#instruments li:nth-child(" + i
					+ ") > div > span > button.button.button-clear.buy.ng-scope.ng-isolate-scope.hint--top.hint--rounded");
		else
			clickfocusOnElement("ul#instruments li:nth-child(" + i
					+ ") > div > span > button.button.button-clear.sell.ng-scope.ng-isolate-scope.hint--top.hint--rounded");

		sleep(50);

		clickElement("form#buysellform div.show-advanced-options");
		clickElement("form#buysellform div.varities-block > label:nth-child(2)");

		findElementByCSS("input#quantity").clear();
		findElementByCSS("input#quantity").sendKeys(quantity);
		findElementByCSS("input#price").clear();
		findElementByCSS("input#price").sendKeys(price);

		findElementByCSS("input#stoploss").sendKeys(stopLoss);
		findElementByCSS("input#squareoff").sendKeys(target);
		//findElementByCSS("input#trailingstoploss").sendKeys(trailingStopLoss);

		clickElement("form#buysellform button[type=\"submit\"]");
		sleep(50);
	}

	public void buySellMISMarket(String i, String quantity, boolean isBuy) {
		focusOnElement("ul#instruments li:nth-child(" + i + ") > div");
		if (isBuy)
			clickfocusOnElement("ul#instruments li:nth-child(" + i
					+ ") > div > span > button.button.button-clear.buy.ng-scope.ng-isolate-scope.hint--top.hint--rounded");
		else
			clickfocusOnElement("ul#instruments li:nth-child(" + i
					+ ") > div > span > button.button.button-clear.sell.ng-scope.ng-isolate-scope.hint--top.hint--rounded");

		sleep(50);

		clickElement("form#buysellform div.five.columns.product > label:nth-child(2)");
		clickElement("form#buysellform div.seven.columns.text-right > label:nth-child(1)");

		findElementByCSS("input#quantity").clear();
		findElementByCSS("input#quantity").sendKeys(quantity);

		clickElement("form#buysellform button[type=\"submit\"]");
		sleep(50);
	}

	public void buySellMISLimit(String i, String quantity, String price, boolean isBuy) {
		focusOnElement("ul#instruments li:nth-child(" + i + ") > div");
		if (isBuy)
			clickfocusOnElement("ul#instruments li:nth-child(" + i
					+ ") > div > span > button.button.button-clear.buy.ng-scope.ng-isolate-scope.hint--top.hint--rounded");
		else
			clickfocusOnElement("ul#instruments li:nth-child(" + i
					+ ") > div > span > button.button.button-clear.sell.ng-scope.ng-isolate-scope.hint--top.hint--rounded");

		sleep(50);

		clickElement("form#buysellform div.five.columns.product > label:nth-child(2)");
		clickElement("form#buysellform div.seven.columns.text-right > label:nth-child(2)");

		findElementByCSS("input#quantity").clear();
		findElementByCSS("input#quantity").sendKeys(quantity);
		findElementByCSS("input#price").sendKeys(price);

		clickElement("form#buysellform button[type=\"submit\"]");
	}

	public void login(String userName, String passWord, String p1, String p2) {
		findElementByCSS("input[name=\"user_id\"]").sendKeys(userName);
		findElementByCSS("input#inputtwo").sendKeys(passWord);
		clickElement("form#loginform button[type=\"submit\"]");

		findElementByCSS("input[name=\"answer1\"]").sendKeys(p1);
		findElementByCSS("input[name=\"answer2\"]").sendKeys(p2);
		clickElement("form#twofaform button[type=\"submit\"]");
	}

	public void readPostion() {
		clickElement("header#header div > nav > ul > li:nth-child(4) > a");
		// TODO read position
	}

	private final void clickElement(String element) {
		if (isDisplayed(element))
			findElementByCSS(element).click();
	}

	private final WebElement findElementByCSS(String element) {
		return (new WebDriverWait(driver, 3))
				.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(element)));
	}

	private final void focusOnElement(String element) {
		new Actions(driver).moveToElement(findElementByCSS(element)).perform();
	}

	private final void clickfocusOnElement(String element) {
		((JavascriptExecutor) driver).executeScript("document.querySelector('" + element + "').click();");
	}
	
	private final String getTextfocusOnElement(String element) {
		return ((WebElement)((JavascriptExecutor) driver).executeScript("return document.querySelector('" + element + "');")).getText();
	}

	private boolean isDisplayed(String element) {
		return findElementByCSS(element).isDisplayed();
	}

	private String getText(String element) {
		if (isDisplayed(element))
			return findElementByCSS(element).getText();
		return "";
	}

	public String getAllOpenOrders() {
		String result = "0";
		clickElement("header#header div > nav > ul > li:nth-child(2) > a");
		if (isDisplayed("div#view div.pending-orders.block.table-wrapper > h1 > span.count.ng-binding.ng-scope"))
			result = findElementByCSS(
					"div#view div.pending-orders.block.table-wrapper > h1 > span.count.ng-binding.ng-scope").getText()
							.replace("(", "").replace(")", "");
		return result;
	}

	public List<String> getOrderTime(String orderNumber) {
		int j = Integer.parseInt(orderNumber);

		List<String> result = new ArrayList<>(j);
		for (int i = 1; i <= Integer.parseInt(orderNumber); i++) {
			result.add(getText(
					"div#view div.pending-orders.block.table-wrapper > table#orders-table-pending > tbody > tr:nth-child("
							+ i + ") > td.order_timestamp.ng-binding"));
		}
		return result;
	}

	public void exitAllOpenOrder() {
		String orderNumber = getAllOpenOrders();
		for (int i = 1; i <= Integer.parseInt(orderNumber); i++) {
			if (LocalTime.of(15, 15).compareTo(LocalTime.now()) > 0)
				clickfocusOnElement("table#orders-table-pending tr:nth-child(" + i
						+ ") > td.action-buttons-container > div > button.btn.btn-red.btn-outline.ng-scope");
			clickElement("div#ngdialog1 button[type=\"button\"].confirm.btn.btn-red");
		}
	}

	public void exitOlderOrder(List<String> orderTime) {
		for (int i = 1; i <= orderTime.size(); i++) {
			if (MINUTES.between(LocalTime.now(), LocalTime.parse(orderTime.get(i - 1))) > 5) {
				focusOnElement("table#orders-table-pending tr:nth-child(" + i + ") > td.action-buttons-container");
				if ("Cancel".equals(getTextfocusOnElement("table#orders-table-pending tr:nth-child(" + i
						+ ") > td.action-buttons-container > div > button.btn.btn-red.btn-outline.ng-scope"))) {
					clickfocusOnElement("table#orders-table-pending tr:nth-child(" + i
							+ ") > td.action-buttons-container > div > button.btn.btn-red.btn-outline.ng-scope");
					clickElement("div#ngdialog1 button[type=\"button\"].confirm.btn.btn-red");
				}
			}
		}
	}
}
