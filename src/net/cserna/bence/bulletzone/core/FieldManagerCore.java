package net.cserna.bence.bulletzone.core;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import net.cserna.bence.bulletzone.entity.Bullet;
import net.cserna.bence.bulletzone.entity.Direction;
import net.cserna.bence.bulletzone.entity.FieldEntity;
import net.cserna.bence.bulletzone.entity.FieldHolder;
import net.cserna.bence.bulletzone.entity.Tank;
import net.cserna.bence.bulletzone.entity.Wall;
import net.cserna.bence.bulletzone.util.SafeCounter;
import android.util.Log;

import com.google.common.base.Optional;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import com.google.inject.Singleton;


/**
* @author Bence Cserna (bence@cserna.net)
*/
@Singleton
public class FieldManagerCore {
	private static final String TAG = "FieldManager";
	private final EventBus eventBus = new EventBus();

	// TODO inject
	/** Field dimensions */
	private static final int FIELD_DIM = 16;
	/** Bullet step time in milliseconds */
	private static final int BULLET_PERIOD = 200;
	/** Bullet's impact effect [life] */
	private static final int BULLET_DAMAGE = 1;
	/** Tank's default life [life] */
	private static final int TANK_LIFE = 3;

	private final ArrayList<FieldHolder> holderGrid = new ArrayList<FieldHolder>();
	private final ConcurrentMap<Long, Tank> tanks = new ConcurrentHashMap<Long, Tank>();
	private final SafeCounter idCounter = new SafeCounter();

	private final Timer timer = new Timer();

	private final Object gridLock = new Object();

	@Inject
	private FieldManagerCore() {
		// Subscribe
		FieldEntity.registerEventBusListener(this);
		// eventBus.register(this);

		// eventBus.post(new Tank(999, "AAA", Direction.Down));

		createFieldHolderGrid();

		// Test // TODO XXX Remove & integrate map loader
		holderGrid.get(1).setFieldEntity(new Wall());
		holderGrid.get(2).setFieldEntity(new Wall());
		holderGrid.get(3).setFieldEntity(new Wall());

		holderGrid.get(17).setFieldEntity(new Wall());
		holderGrid.get(33).setFieldEntity(new Wall());
		holderGrid.get(49).setFieldEntity(new Wall());
		holderGrid.get(65).setFieldEntity(new Wall());
		
		holderGrid.get(34).setFieldEntity(new Wall());
		holderGrid.get(66).setFieldEntity(new Wall());
		
		holderGrid.get(35).setFieldEntity(new Wall());
		holderGrid.get(51).setFieldEntity(new Wall());
		holderGrid.get(67).setFieldEntity(new Wall());
		
		holderGrid.get(5).setFieldEntity(new Wall());
		holderGrid.get(21).setFieldEntity(new Wall());
		holderGrid.get(37).setFieldEntity(new Wall());
		holderGrid.get(53).setFieldEntity(new Wall());
		holderGrid.get(69).setFieldEntity(new Wall());
		
		holderGrid.get(7).setFieldEntity(new Wall());
		holderGrid.get(23).setFieldEntity(new Wall());
		holderGrid.get(39).setFieldEntity(new Wall());
		holderGrid.get(71).setFieldEntity(new Wall());
		
		holderGrid.get(8).setFieldEntity(new Wall());
		holderGrid.get(40).setFieldEntity(new Wall());
		holderGrid.get(72).setFieldEntity(new Wall());
		
		holderGrid.get(9).setFieldEntity(new Wall());
		holderGrid.get(25).setFieldEntity(new Wall());
		holderGrid.get(41).setFieldEntity(new Wall());
		holderGrid.get(57).setFieldEntity(new Wall());
		holderGrid.get(73).setFieldEntity(new Wall());
		
	}

	private void createFieldHolderGrid() {
		synchronized (gridLock) {
			// holderGrid.clear();
			for (int i = 0; i < FIELD_DIM * FIELD_DIM; i++) {
				holderGrid.add(new FieldHolder());
			}

			FieldHolder targetHolder;
			FieldHolder rightHolder;
			FieldHolder downHolder;

			// Build connections
			for (int i = 0; i < FIELD_DIM; i++) {
				for (int j = 0; j < FIELD_DIM; j++) {
					targetHolder = holderGrid.get(i * FIELD_DIM + j);
					rightHolder = holderGrid.get(i * FIELD_DIM
							+ ((j + 1) % FIELD_DIM));
					downHolder = holderGrid.get(((i + 1) % FIELD_DIM)
							* FIELD_DIM + j);

					targetHolder.addNeighbor(Direction.Right, rightHolder);
					rightHolder.addNeighbor(Direction.Left, targetHolder);

					targetHolder.addNeighbor(Direction.Down, downHolder);
					downHolder.addNeighbor(Direction.Up, targetHolder);
				}
			}
		}
	}

	public void notifyHolderGridChanged() {
		synchronized (holderGrid) {
			List<Optional<FieldEntity>> entities = new ArrayList<Optional<FieldEntity>>();

			FieldEntity entity;
			for (FieldHolder holder : holderGrid) {
				if (holder.isPresent()) {
					entity = holder.getEntity();
					entity = entity.copy();

					entities.add(Optional.<FieldEntity> of(entity));
				} else {
					entities.add(Optional.<FieldEntity> absent());
				}
			}

			eventBus.post(entities);
		}
	}

	public boolean turnTank(long tankId, Direction direction) {
		checkNotNull(direction);
		synchronized (gridLock) {

			// Find user
			Tank tank = tanks.get(tankId);
			if (tank == null) {
				Log.i(TAG, "Cannot find user with id: " + tankId);
				return false;
			}
			
			tank.setDirection(direction);

			notifyHolderGridChanged();
			return true; // TODO check
		}
	}

	public boolean fireBullet(long tankId) {
		synchronized (gridLock) {
			// Find tank
			Tank tank = tanks.get(tankId);
			if (tank == null) {
				Log.i(TAG, "Cannot find user with id: " + tankId);
				return false;
			}
			
			Direction direction = tank.getDirection();
			FieldHolder parent = tank.getParent();

			// Create a new bullet to fire
			final Bullet bullet = new Bullet(tankId, direction, BULLET_DAMAGE);
			// Set the same parent for the bullet.
			// This should be only a one way reference.
			bullet.setParent(parent);

			// TODO make it nicer
			timer.schedule(new TimerTask() {

				@Override
				public void run() {
					synchronized (gridLock) {
						FieldHolder currentField = bullet.getParent();
						Direction direction = bullet.getDirection();
						FieldHolder nextField = currentField
								.getNeighbor(direction);

						// Is the bullet visible on the field?
						boolean isVisible = currentField.isPresent()
								&& (currentField.getEntity() == bullet);

						if (nextField.isPresent()) {
							// Something is there, hit it
							nextField.getEntity().hit(bullet.getDamage());

							if (isVisible) {
								// Remove bullet from field
								currentField.clearField();
							}
							cancel();

						} else {
							if (isVisible) {
								// Remove bullet from field
								currentField.clearField();
							}

							nextField.setFieldEntity(bullet);
							bullet.setParent(nextField);
						}

						notifyHolderGridChanged();
					}

				}
			}, 0, BULLET_PERIOD);

			notifyHolderGridChanged();
			return true;
		}
	}

	public boolean moveUser(long tankId, Direction direction) {
		synchronized (gridLock) {
			// Find tank
			Tank tank = tanks.get(tankId);
			if (tank == null) {
				Log.i(TAG, "Cannot find user with id: " + tankId);
				return false;
			}

			FieldHolder parent = tank.getParent();
			FieldHolder nextField = checkNotNull(parent.getNeighbor(direction),
					"Neightbor is not available");

			boolean isCompleted;
			if (!nextField.isPresent()) {
				// If the next field is empty move the user
				parent.clearField();
				nextField.setFieldEntity(tank);
				tank.setParent(nextField);

				isCompleted = true;
			} else {
				isCompleted = false;
			}

			notifyHolderGridChanged();
			return isCompleted;
		}
	}

	/**
	 * Create a tank with unique id and place it on the map at a random place.
	 * 
	 * @return Tank's unique identifier.
	 */
	public long createAndPlaceNewTank(String name) {
		synchronized (gridLock) {
			// Create a new unique tank
			int id = idCounter.increment();
			Tank tank = new Tank(id, name, Direction.Up);
			tank.setLife(TANK_LIFE);

			Random random = new Random();
			int x;
			int y;

			// This may run for forever.. If there is no free space. XXX
			for (;;) {
				x = random.nextInt(FIELD_DIM);
				y = random.nextInt(FIELD_DIM);
				FieldHolder fieldElement = holderGrid.get(x * FIELD_DIM + y);
				if (!fieldElement.isPresent()) {
					fieldElement.setFieldEntity(tank);
					tank.setParent(fieldElement);
					break;
				}
			}

			tanks.put((long) id, tank);
			notifyHolderGridChanged();
			return id;
		}
	}

	@Subscribe
	public void onEvent(Tank tank) {
		checkNotNull(tank);

		Log.d(TAG, "Tank event: remove tank");

		synchronized (gridLock) {
			tanks.remove(tank.getId());
			tank.getParent().clearField();
			tank.setParent(null);
		}
	}

	public void registerFieldManagerEventBus(Object listener) {
		checkNotNull(listener);
		eventBus.register(listener);
	}

	public void unregisterFieldManagerEventBus(Object handler) {
		checkNotNull(handler);
		eventBus.unregister(handler);
	}

}
