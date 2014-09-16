package net.cserna.bence.bulletzone.core;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;

import android.util.Log;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import net.cserna.bence.bulletzone.action.Action;
import net.cserna.bence.bulletzone.action.ActionCreateTank;
import net.cserna.bence.bulletzone.action.ActionFire;
import net.cserna.bence.bulletzone.action.ActionMove;
import net.cserna.bence.bulletzone.action.ActionPoisonPill;
import net.cserna.bence.bulletzone.action.ActionTurn;
import net.cserna.bence.bulletzone.action.ActionReply;
import net.cserna.bence.bulletzone.entity.Direction;

/**
* @author Bence Cserna (bence@cserna.net)
*/
@Singleton
public class AsyncFieldManager {
	public static final String TAG = "AsyncFieldManager";

	private final BlockingQueue<Action> actionQueue = new LinkedBlockingQueue<Action>();
	private ActionDispatcher dispathcer;

	private final FieldManagerCore fieldManagerCore;
	private final CountDownLatch latch = new CountDownLatch(1);

	@Inject
	private AsyncFieldManager(FieldManagerCore fieldManagerCore) {
		this.fieldManagerCore = fieldManagerCore;

		// Start action processor thread
		processActions();
	}

	public void processActions() {
		// TODO This should be cleaned up
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					latch.await();
				} catch (InterruptedException e1) {
					Log.e(TAG, "Start block interrupted");
				}
				
				Action action;
				for (;;) {
					try {
						action = actionQueue.take();
						if (action.getType() == -1) {
							break;
						} else {
							processAction(action);
						}
					} catch (InterruptedException e) {
						Log.e(TAG, "Procees action failed.", e);
					}
				}
			}
		}).start();
	}

	public void addAction(Action action) {
		checkNotNull(action, "Add action failed: Action cannot be null.");
		try {
			actionQueue.put(action);
		} catch (InterruptedException e) {
			Log.e(TAG, "Add Action to queue failed.", e);
		}
	}

	public void stop() {
		addAction(new ActionPoisonPill());
	}

	private void processAction(Action action) throws InterruptedException {
		int type = action.getType();

		switch (type) {
		case ActionMove.TYPE:
			ActionMove actionMove = (ActionMove) action;
			long userId = actionMove.getUserId();
			Direction direction = actionMove.direction;

			boolean isCompleted = fieldManagerCore.moveUser(userId, direction);
			dispathcer.dispatchReply(new ActionReply(action, isCompleted, null));
			break;

		case ActionFire.TYPE:
			ActionFire actionFire = (ActionFire) action;
			Log.d(TAG, "ProcAction:" + actionFire.getUserId());

			fieldManagerCore.fireBullet(actionFire.getUserId());
			// TODO reply
			break;

		case ActionTurn.TYPE:
			ActionTurn actionTurn = (ActionTurn) action;
			fieldManagerCore.turnTank(actionTurn.getUserId(),
					actionTurn.direction);
			dispathcer.dispatchReply(new ActionReply(action, true, null));
			break;

		case ActionCreateTank.TYPE:
			ActionCreateTank actionCreateTank = (ActionCreateTank) action;
			String name = actionCreateTank.getName();
			long tankId = fieldManagerCore.createAndPlaceNewTank(name);
			dispathcer.dispatchReply(new ActionReply(action, true, tankId));
			break;

		default:
			Log.e(TAG, "Unknown Action");
			break;
		}
	}

	public void startAsyncFieldManager (
			ActionDispatcher dispathcer) {
				this.dispathcer = checkNotNull(dispathcer);
		latch.countDown();
	}
}
