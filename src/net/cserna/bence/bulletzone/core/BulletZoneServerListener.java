package net.cserna.bence.bulletzone.core;

/**
* @author Bence Cserna (bence@cserna.net)
*/
public interface BulletZoneServerListener {
	/**
	 * Called in case of field update.
	 * 
	 * @param field
	 *            current field state.
	 * @param timestamp
	 *            server's system time at the update.
	 */
	public void onFieldUpdate(int[][] field, long timestamp);
	
	/**
	 * For future use.
	 * 
	 * @param code
	 *            - Event code.
	 * @param object
	 */
	public void onEvent(int code, Object object);
}
