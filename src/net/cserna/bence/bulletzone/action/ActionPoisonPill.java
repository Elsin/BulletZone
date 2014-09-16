package net.cserna.bence.bulletzone.action;

public class ActionPoisonPill extends Action {
	
	public ActionPoisonPill() {
		super(-1);
	}

	public static final byte TYPE = -1;

	@Override
	public byte getType() {
		return TYPE;
	}

}
