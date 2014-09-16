package net.cserna.bence.bulletzone.action;

import static com.google.common.base.Preconditions.checkNotNull;
import net.cserna.bence.bulletzone.entity.Direction;

public class ActionMove extends Action {
	
	public static final byte TYPE = 1;
	public final Direction direction;

	public ActionMove(long userId, Direction direction) {
		super(userId);
		this.direction = checkNotNull(direction);
	}
	
	@Override
	public byte getType() {
		return TYPE;
	}

}
