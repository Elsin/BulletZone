package net.cserna.bence.bulletzone.action;

import static com.google.common.base.Preconditions.checkNotNull;
import net.cserna.bence.bulletzone.entity.Direction;

public class ActionTurn extends Action {
	
	public static final byte TYPE = 2;
	public final Direction direction;
	
	public ActionTurn(long userId, Direction direction) {
		super(userId);
		this.direction = checkNotNull(direction);
	}
	
	@Override
	public byte getType() {
		return TYPE;
	}
}
