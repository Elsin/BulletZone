package net.cserna.bence.bulletzone;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.List;

import net.cserna.bence.bulletzone.action.ActionCreateTank;
import net.cserna.bence.bulletzone.action.ActionFire;
import net.cserna.bence.bulletzone.action.ActionMove;
import net.cserna.bence.bulletzone.action.ActionReply;
import net.cserna.bence.bulletzone.core.ActionDispatcher;
import net.cserna.bence.bulletzone.core.AsyncFieldManager;
import net.cserna.bence.bulletzone.core.BulletZoneConnection;
import net.cserna.bence.bulletzone.core.BulletZoneModule;
import net.cserna.bence.bulletzone.core.FieldManagerCore;
import net.cserna.bence.bulletzone.entity.Direction;
import net.cserna.bence.bulletzone.entity.FieldEntity;
import net.cserna.bence.bulletzone.entity.FieldHolder;
import net.cserna.bence.bulletzone.network.NetworkModule;
import net.cserna.bence.bulletzone.ui.GridAdapter;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.GridView;

import com.google.common.base.Optional;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.Bean;
import com.googlecode.androidannotations.annotations.Click;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.UiThread;
import com.googlecode.androidannotations.annotations.ViewById;

/**
 * Activity to test the server functionality through the asyncron interface.
 * 
 * @author Bence Cserna (bence@cserna.net)
 */
@EActivity(R.layout.activity_control)
public class ControlActivity extends Activity implements BulletZoneConnection {
	private static final String TAG = "ControlActivity";

	@Bean
	protected GridAdapter mGridAdapter;

	@ViewById
	protected GridView gridView;

	private FieldManagerCore mFieldManager;
	private ActionDispatcher mDispathcer;

	private long tankId = -1;

	@AfterViews
	protected void afterViewInjection() {
		initialize();

		gridView.setAdapter(mGridAdapter);
		mFieldManager.registerFieldManagerEventBus(this);

		mDispathcer.registerConnection(this);
		mDispathcer.handleAction(new ActionCreateTank("Zeus"));

		mFieldManager.notifyHolderGridChanged();

	}

	private void initialize() {
		Injector injector = Guice.createInjector(new BulletZoneModule(),
				new NetworkModule());
		mFieldManager = injector.getInstance(FieldManagerCore.class);
		mDispathcer = injector.getInstance(ActionDispatcher.class);
	}

	@Subscribe
	@UiThread
	public void onFieldChanged(List<Optional<FieldEntity>> entities) {
		mGridAdapter.updateList(entities);
	}

	@Click({ R.id.buttonUp, R.id.buttonDown, R.id.buttonLeft, R.id.buttonRight })
	protected void onButtonMove(View view) {
		final int viewId = view.getId();
		Direction moveDirection = null;

		switch (viewId) {
		case R.id.buttonUp:
			moveDirection = Direction.Up;
			break;
		case R.id.buttonDown:
			moveDirection = Direction.Down;
			break;
		case R.id.buttonLeft:
			moveDirection = Direction.Left;
			break;
		case R.id.buttonRight:
			moveDirection = Direction.Right;
			break;
		default:
			Log.e(TAG, "Unknown movement button id: " + viewId);
			break;
		}
		if (moveDirection != null && tankId != -1) {
			ActionMove actionMove = new ActionMove(tankId, moveDirection);

			mDispathcer.handleAction(actionMove);
		}

	}

	@Click(R.id.buttonFire)
	protected void onButtonFire() {
		if (tankId != -1) {
			Log.d(TAG, "Fire: " + tankId);
			mDispathcer.handleAction(new ActionFire(tankId));
		}
	}

	// @Background
	// protected void processReplys() {
	// Reply reply;
	// for (;;) {
	// try {
	// reply = fieldManager.waitForReplay();
	//
	// if (reply.action.getType() == -1) {
	// break;
	// } else {
	// processReply(reply);
	// }
	// } catch (InterruptedException e) {
	// Log.e(TAG, "Process replys failed", e);
	// }
	// }
	// }

	@Override
	public void handle(ActionReply reply) {
		checkNotNull(reply);

		if (reply.action.getType() == ActionCreateTank.TYPE) {
			tankId = (Long) reply.extra;
			Log.d(TAG, "Local TankId has been received: " + tankId);
		}
	}
}
