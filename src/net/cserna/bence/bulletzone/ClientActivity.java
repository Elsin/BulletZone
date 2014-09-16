package net.cserna.bence.bulletzone;

import java.util.ArrayList;
import java.util.List;

import net.cserna.bence.bulletzone.core.BulletZoneLocalProxy;
import net.cserna.bence.bulletzone.core.BulletZoneServer;
import net.cserna.bence.bulletzone.core.BulletZoneServerListener;
import net.cserna.bence.bulletzone.entity.FieldEntity;
import net.cserna.bence.bulletzone.entity.NumberField;
import net.cserna.bence.bulletzone.ui.GridAdapter;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.GridView;

import com.google.common.base.Optional;
import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.Background;
import com.googlecode.androidannotations.annotations.Bean;
import com.googlecode.androidannotations.annotations.Click;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.UiThread;
import com.googlecode.androidannotations.annotations.ViewById;

/**
 * Example implementation of the BulletZone API.
 * 
 * @author Bence Cserna (bence@cserna.net)
 */
@EActivity(R.layout.activity_control)
public class ClientActivity extends Activity implements
		BulletZoneServerListener {
	private static final String TAG = "ClientActivity";

	@Bean
	protected GridAdapter mGridAdapter;

	/** Visual representation of the game field */
	@ViewById
	protected GridView gridView;

	/** Remote tank identifier */
	private long tankId = -1;

	/** {@link BulletZoneServer} instance */
	private BulletZoneServer server;

	@AfterViews
	protected void afterViewInjection() {
		// Join to server after the view initialization
		server = BulletZoneLocalProxy.createBulletZoneServer();
		server.joinServer(null, "Zeus", this);

		gridView.setAdapter(mGridAdapter);
	}

	/**
	 * Click handler function to handle movement events.
	 * @param view
	 */
	@Click({ R.id.buttonUp, R.id.buttonDown, R.id.buttonLeft, R.id.buttonRight })
	protected void onButtonMove(View view) {
		final int viewId = view.getId();
		byte direction = 0;

		switch (viewId) {
		case R.id.buttonUp:
			direction = 0;
			break;
		case R.id.buttonDown:
			direction = 4;
			break;
		case R.id.buttonLeft:
			direction = 6;
			break;
		case R.id.buttonRight:
			direction = 2;
			break;
		default:
			Log.e(TAG, "Unknown movement button id: " + viewId);
			break;
		}

		server.move(direction);
	}

	/**
	 * 
	 */
	@Click(R.id.buttonFire)
	@Background
	protected void onButtonFire() {
		server.fireBullet();
	}

	@UiThread
	public void onFieldChanged(List<Optional<FieldEntity>> entities) {
		mGridAdapter.updateList(entities);
	}

	@Override
	public void onFieldUpdate(int[][] field, long timestamp) {
		List<Optional<FieldEntity>> entities = new ArrayList<Optional<FieldEntity>>();

		for (int i = 0; i < 16; i++) {
			for (int j = 0; j < 16; j++) {
				if (field[i][j] == 0) {
					entities.add(Optional.<FieldEntity> absent());
				} else {
					entities.add(Optional.<FieldEntity> of(new NumberField(
							field[i][j])));
				}
			}
		}

		onFieldChanged(entities);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onEvent(int code, Object entities) {
		onFieldChanged((List<Optional<FieldEntity>>) entities);
	}

}
