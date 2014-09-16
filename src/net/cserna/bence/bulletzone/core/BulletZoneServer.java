package net.cserna.bence.bulletzone.core;

import java.net.InetAddress;

/**
 * @author Bence Cserna (bence@cserna.net)
 */
public interface BulletZoneServer {
	/**
	 * Call to join BulletZone server.
	 * 
	 * <p/>
	 * <b>Execution of this method can take up to few seconds.</b>
	 * @param address
	 *            BulletZone server's IP address.
	 * @param playerName
	 *            Name of the player.
	 * @param listener
	 *            BulletZone server callback.
	 * @return Tank id. Can be used to identify your tank on battlefield.
	 */
	public int joinServer(InetAddress address, String playerName,
			BulletZoneServerListener listener);

	/**
	 * Turn tank to the given direction.
	 * <p/>
	 * <b>Execution of this method can take up to few seconds.</b>
	 * 
	 * @param direction
	 *            {0 - UP, 2 - RIGHT, 4 - DOWN, 6 - LEFT}
	 * @return <code>true</code>, if operation was successfully completed.
	 */
	public boolean turn(byte direction);

	/**
	 * Move tank to the given direction.
	 * <p/>
	 * <b>Execution of this method can take up to few seconds.</b>
	 * 
	 * @param direction
	 *            {0 - UP, 2 - RIGHT, 4 - DOWN, 6 - LEFT}
	 * @return <code>true</code>, if operation was successfully completed.
	 */
	public boolean move(byte direction);

	/**
	 * Fire a bullet.
	 * <p/>
	 * <b>Execution of this method can take up to few seconds.</b>
	 * 
	 * @return <code>true</code>, if operation was successfully completed.
	 */
	public boolean fireBullet();

}
