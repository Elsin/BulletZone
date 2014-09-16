package net.cserna.bence.bulletzone.action;

/**
 * Base remote action class.
 * 
 * @author Bence Cserna (bence@cserna.net)
 */
public abstract class Action {

	private final long userId;
	private transient long connectionId;

	public Action(long userId) {
		this.userId = userId;
	}
	
	public abstract byte getType();

	public long getUserId() {
		return userId;
	}

	public long getConnectionId() {
		return connectionId;
	}

	public void setConnectionId(long connectionId) {
		this.connectionId = connectionId;
	}
}
