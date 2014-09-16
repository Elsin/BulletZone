package net.cserna.bence.bulletzone.core;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import android.util.Log;
import net.cserna.bence.bulletzone.action.Action;
import net.cserna.bence.bulletzone.action.ActionReply;
import net.cserna.bence.bulletzone.util.SafeCounter;

/**
* @author Bence Cserna (bence@cserna.net)
*/
@Singleton
public class ThreadBasedActionDispatcher implements ActionDispatcher {
	private static final String TAG = "ActionDispathcer";

	private final ThreadLocal<Long> localId = new ThreadLocal<Long>();
	private final ConcurrentMap<Long, BulletZoneConnection> connections = new ConcurrentHashMap<Long, BulletZoneConnection>();
	private final SafeCounter counter = new SafeCounter();
	private final AsyncFieldManager asyncFieldManager;
	
	@Inject
	private ThreadBasedActionDispatcher(AsyncFieldManager asyncFieldManager) {
		this.asyncFieldManager = asyncFieldManager;
		
		asyncFieldManager.startAsyncFieldManager(this);
	}

	/**
	 * @see net.cserna.bence.bulletzone.core.ActionDispatcher#registerConnection(net.cserna.bence.bulletzone.core.BulletZoneConnection)
	 */
	@Override
	public void registerConnection(BulletZoneConnection connection) {
		checkNotNull(connection);

		long connectionId = counter.increment();
		localId.set(connectionId);
		connections.put(connectionId, connection);
	}

	/**
	 * @see net.cserna.bence.bulletzone.core.ActionDispatcher#unregisterConnection()
	 */
	@Override
	public void unregisterConnection() {
		connections.remove(localId.get());
		localId.remove();
	}

	/**
	 * @see net.cserna.bence.bulletzone.core.ActionDispatcher#handleAction(net.cserna.bence.bulletzone.action.Action)
	 */
	@Override
	public void handleAction(Action action) {
		checkNotNull(action);

		// Label message
		action.setConnectionId(localId.get());
		if (asyncFieldManager != null) {
			asyncFieldManager.addAction(action);
		}
	}

	/**
	 * @see net.cserna.bence.bulletzone.core.ActionDispatcher#dispatchReply(net.cserna.bence.bulletzone.action.ActionReply)
	 */
	@Override
	public void dispatchReply(ActionReply reply) {
		checkNotNull(reply);

		long connectionId = reply.action.getConnectionId();
		BulletZoneConnection connection = connections.get(connectionId);

		if (connection != null) {
			connection.handle(reply);
		} else {
			Log.e(TAG, "Connection not found. Reply failed.");
		}
	}
}
