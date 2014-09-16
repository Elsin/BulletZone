package net.cserna.bence.bulletzone.action;

public class ActionFire extends Action {
	
	public static final byte TYPE = 3;
	
	public ActionFire(long userId) {
		super(userId);
	}

	@Override
	public byte getType() {
		return TYPE;
	}

}
