package net.cserna.bence.bulletzone.ui;

import java.util.ArrayList;
import java.util.List;

import net.cserna.bence.bulletzone.R;
import net.cserna.bence.bulletzone.entity.FieldEntity;
import net.cserna.bence.bulletzone.entity.FieldHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.google.common.base.Optional;
import com.googlecode.androidannotations.annotations.EBean;
import com.googlecode.androidannotations.annotations.SystemService;

@EBean
public class GridAdapter extends BaseAdapter {
	
	private List<Optional<FieldEntity>> mEntities = new ArrayList<Optional<FieldEntity>>();
	private final Object updateLock = new Object();
	
	@SystemService
	protected LayoutInflater inflater;

	public void updateList(List<Optional<FieldEntity>> entities) {
		synchronized (updateLock) {
			this.mEntities = entities;
			this.notifyDataSetChanged();
		}
	}
	
	@Override
	public int getCount() {
		return mEntities.size();
	}

	@Override
	public Object getItem(int position) {
		return mEntities.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.field_item, null);
		}
		
		Optional<FieldEntity> optional = mEntities.get(position);
		
		if (convertView instanceof TextView) {
			synchronized (updateLock) {
				if (optional.isPresent()) {
					((TextView) convertView).setText(optional.get().toString());
				} else {
					((TextView) convertView).setText("");
				}
			}
		}
		
		return convertView;
	}
}


