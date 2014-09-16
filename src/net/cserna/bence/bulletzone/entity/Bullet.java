package net.cserna.bence.bulletzone.entity;


public class Bullet extends FieldEntity {

	private long userId;
	private Direction direction;
	private int damage;

	public Bullet(long userId, Direction direction, int damage) {
		this.damage = damage;
		this.setUserId(userId);
		this.setDirection(direction);
	}

	@Override
	public int getIntValue() {
		return (int) (2000000 + 1000 * userId + 1);
	}

	@Override
	public String toString() {
		return "B";
	}

	@Override
	public FieldEntity copy() {
		return new Bullet(userId, direction, damage);
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public Direction getDirection() {
		return direction;
	}

	public void setDirection(Direction direction) {
		this.direction = direction;
	}

	public int getDamage() {
		return damage;
	}

	public void setDamage(int damage) {
		this.damage = damage;
	}

}
