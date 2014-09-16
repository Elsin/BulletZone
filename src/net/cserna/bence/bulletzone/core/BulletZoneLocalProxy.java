package net.cserna.bence.bulletzone.core;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.net.InetAddress;
import java.util.List;
import java.util.Random;

import net.cserna.bence.bulletzone.entity.Direction;
import net.cserna.bence.bulletzone.entity.FieldEntity;
import net.cserna.bence.bulletzone.network.NetworkModule;

import com.google.common.base.Optional;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;

public class BulletZoneLocalProxy implements BulletZoneServer {

	@SuppressWarnings("unused")
	private static final String TAG = "BulletZoneLocalProxy";
	
	private FieldManagerCore fieldManagerCore;
	private BulletZoneServerListener listener;
	private long tankId = -1;

	public static BulletZoneServer createBulletZoneServer() {
		Injector injector = Guice.createInjector(new BulletZoneModule(),
				new NetworkModule());
		return injector.getInstance(BulletZoneServer.class);
	}

	@Inject
	private BulletZoneLocalProxy(FieldManagerCore fieldManagerCore) {
		this.fieldManagerCore = fieldManagerCore;

		fieldManagerCore.registerFieldManagerEventBus(this);
	}

	@Override
	public int joinServer(InetAddress address, String playerName,
			BulletZoneServerListener listener) {
		checkNotNull(playerName, "Player name cannot be null");
		checkNotNull(listener, "Server listener cannot be null");

		this.listener = listener;

		tankId = fieldManagerCore.createAndPlaceNewTank(playerName);

		return (int) tankId;
	}

	@Override
	public boolean turn(byte directionByte) {
		checkState(tankId >= 0, "Join server should be called first");

		Direction direction = Direction.fromByte(directionByte);
		boolean isSuccessful = fieldManagerCore.turnTank(tankId, direction);
		delay();

		return isSuccessful;
	}

	@Override
	public boolean move(byte directionByte) {
		checkState(tankId >= 0, "Join server should be called first");

		Direction direction = Direction.fromByte(directionByte);
		boolean isSuccessful = fieldManagerCore.moveUser(tankId, direction);

		delay();
		return isSuccessful;
	}

	@Override
	public boolean fireBullet() {
		checkState(tankId >= 0, "Join server should be called first");

		boolean isSuccessful = fieldManagerCore.fireBullet(tankId);

		delay();
		return isSuccessful;
	}

	private void delay() {
		Random random = new Random();
		try {
			Thread.sleep(random.nextInt(1500));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Subscribe
	public void onFieldChanged(List<Optional<FieldEntity>> entities) {
		int[][] field = new int[16][16];

		for (int i = 0; i < 16; i++) {
			for (int j = 0; j < 16; j++) {
				Optional<FieldEntity> optional = entities.get(i * 16 + j);
				field[i][j] = optional.isPresent() ? optional.get()
						.getIntValue() : 0;
						
//				Log.d(TAG, "Value: " + field[i][j]);
			}
		}

		listener.onFieldUpdate(field, System.currentTimeMillis());
//		listener.onEvent(0, entities);
	}
}
