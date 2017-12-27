package com;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CleanOrders {
	@Autowired
	private EventExecutor eventExecutor;

	public void clearOldOrders() throws Exception {

		new Thread(() -> {
			while (true) {
				//sleep(1000 * 60 * 5);
				eventExecutor.getQueue().add(new StockMessage(Integer.MIN_VALUE, "ClearOldOrders", null));
			}
		}).start();
	}
	
	public void clearAllOrders() throws Exception {

		new Thread(() -> {
			while (true) {
				eventExecutor.getQueue().add(new StockMessage(Integer.MIN_VALUE, "ClearAllOrders", null));
			}
		}).start();
	}

	public static final void sleep(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
