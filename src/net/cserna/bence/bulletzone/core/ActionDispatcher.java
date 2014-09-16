package net.cserna.bence.bulletzone.core;

import net.cserna.bence.bulletzone.action.Action;
import net.cserna.bence.bulletzone.action.ActionReply;


/**
* @author Bence Cserna (bence@cserna.net)
*/
public interface ActionDispatcher {

	/**
	 * This should be called from the connection's sender thread.
	 * 
	 * @param connection
	 */
	public abstract void registerConnection(BulletZoneConnection connection);

	/**
	 * This should be called from the connection's sender thread.
	 * 
	 * @param connection
	 */
	public abstract void unregisterConnection();

	public abstract void handleAction(Action action);

	public abstract void dispatchReply(ActionReply reply);
}