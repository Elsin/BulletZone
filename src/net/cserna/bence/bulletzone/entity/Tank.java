package net.cserna.bence.bulletzone.entity;


public class Tank extends FieldEntity {

	@SuppressWarnings("unused")
	private static final String TAG = "Tank";

	private final long id;

	private final String name;

	private long lastMoveTime;
	private int allowedMoveInterval;

	private long lastFireTime;
	private int allowedFireInterval;

	private int numberOfBullets;
	private int allowedNumberOfBullets;

	private int life;

	private Direction direction;

	public Tank(long id, String name, Direction direction) {
		this.id = id;
		this.name = name;
		this.direction = direction;
	}

	@Override
	public FieldEntity copy() {
		return new Tank(id, name, direction);
	}

	@Override
	public void hit(int damage) {
		life = life - damage;
//		Log.d(TAG, "TankId: " + id + " hit -> life: " + life);

		if (life <= 0) {
//			Log.d(TAG, "Tank event");
			eventBus.post(Tank.this);
			eventBus.post(new Object());
		}
	}

	public long getLastMoveTime() {
		return lastMoveTime;
	}

	public void setLastMoveTime(long lastMoveTime) {
		this.lastMoveTime = lastMoveTime;
	}

	public long getAllowedMoveInterval() {
		return allowedMoveInterval;
	}

	public void setAllowedMoveInterval(int allowedMoveInterval) {
		this.allowedMoveInterval = allowedMoveInterval;
	}

	public long getLastFireTime() {
		return lastFireTime;
	}

	public void setLastFireTime(long lastFireTime) {
		this.lastFireTime = lastFireTime;
	}

	public long getAllowedFireInterval() {
		return allowedFireInterval;
	}

	public void setAllowedFireInterval(int allowedFireInterval) {
		this.allowedFireInterval = allowedFireInterval;
	}

	public int getNumberOfBullets() {
		return numberOfBullets;
	}

	public void setNumberOfBullets(int numberOfBullets) {
		this.numberOfBullets = numberOfBullets;
	}

	public int getAllowedNumberOfBullets() {
		return allowedNumberOfBullets;
	}

	public void setAllowedNumberOfBullets(int allowedNumberOfBullets) {
		this.allowedNumberOfBullets = allowedNumberOfBullets;
	}

	public Direction getDirection() {
		return direction;
	}

	public void setDirection(Direction direction) {
		this.direction = direction;
	}

	public long getId() {
		return id;
	}

	@Override
	public int getIntValue() {
		return (int) (10000000 + 10000 * id + 10 * life + Direction
				.toByte(direction));
	}

	@Override
	public String toString() {
		return "T";
	}

	public String getName() {
		return name;
	}

	public int getLife() {
		return life;
	}

	public void setLife(int life) {
		this.life = life;
	}

}
