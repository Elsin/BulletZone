package net.cserna.bence.bulletzone.action;

public class ActionCreateTank extends Action {

	public static final byte TYPE = 4;
	private final String name;
	
	public ActionCreateTank(String name) {
		super(-1);
		this.name = name;
	}
	
	@Override
	public byte getType() {
		return 4;
	}

	public String getName() {
		return name;
	}
}
