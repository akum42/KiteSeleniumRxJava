package com;

import java.time.LocalTime;

public class Util {

	public static final void sleep(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public static int isMarketOpen() {
		return LocalTime.of(15, 15).compareTo(LocalTime.now()) * LocalTime.now().compareTo(LocalTime.of(9, 15));
	}
}
