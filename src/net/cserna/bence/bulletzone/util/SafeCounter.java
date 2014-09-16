package net.cserna.bence.bulletzone.util;

public class SafeCounter {

	private int counter;
	private Object counterLock = new Object();

	public void reset() {
		synchronized (counterLock) {
			counter = 0;
		}
	}

	/**
	 * Increment the counter value.
	 * @return The incremented value.
	 */
	public int increment() {
		synchronized (counterLock) {
			counter++;
			return counter;
		}
	}

}
