package net.cserna.bence.bulletzone.action;

public class ActionReply extends Action {
	
	private static final byte TYPE = 5;
	public final Action action;
	public final boolean wasSuccessful;
	public final Object extra;
	
	public ActionReply(Action action, boolean wasSuccessful, Object extra) {
		super(action.getUserId());
		this.action = action;
		this.wasSuccessful = wasSuccessful;
		this.extra = extra;
	}

	@Override
	public byte getType() {
		return TYPE;
	}

}
