package net.cserna.bence.bulletzone.action;

public class ActionUpdate extends Action {

	private static final byte TYPE = 6;
	private final int[][] field;
	private final long timestamp;

	public ActionUpdate(long userId, int[][] field, long timestamp) {
		super(userId);
		this.field = field;
		this.timestamp = timestamp;
	}

	@Override
	public byte getType() {
		return TYPE;
	}

}
