/*******************************************************************************
 * Copyright (c) 2014 Nordic Semiconductor. All Rights Reserved.
 * 
 * The information contained herein is property of Nordic Semiconductor ASA.
 * Terms and conditions of usage are described in detail in NORDIC SEMICONDUCTOR STANDARD SOFTWARE LICENSE AGREEMENT.
 * Licensees are granted free, non-transferable use of the information. NO WARRANTY of ANY KIND is provided. 
 * This heading must NOT be removed from the file.
 ******************************************************************************/
package no.nordicsemi.android.nrfbeacon.beacon.adapter;

import java.util.List;

import com.vatsi.locus.R;
import no.nordicsemi.android.nrfbeacon.database.BeaconContract;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * Provides the list of all saved regions.
 */
public class BeaconAdapter extends CursorAdapter {
	private final PackageManager mPackageManager;
	private final LayoutInflater mInflater;

	public BeaconAdapter(final Context context, final Cursor c) {
		super(context, c, 0);

		mPackageManager = context.getPackageManager();
		mInflater = LayoutInflater.from(context);
	}

	@Override
	public View newView(final Context context, final Cursor cursor, final ViewGroup parent) {
		final View view = mInflater.inflate(R.layout.fragment_beacons_item, parent, false);

		final ViewHolder holder = new ViewHolder();
		holder.name = (TextView) view.findViewById(R.id.name);
		holder.signal = (ProgressBar) view.findViewById(R.id.progress);
		holder.event = (ImageView) view.findViewById(R.id.event);
		holder.action = (ImageView) view.findViewById(R.id.action);
		view.setTag(holder);
		return view;
	}

	@Override
	public void bindView(final View view, final Context context, final Cursor cursor) {
		final boolean enabled = cursor.getInt(9 /* ENABLED */) == 1;

		final ViewHolder holder = (ViewHolder) view.getTag();
		holder.name.setText(cursor.getString(1 /* NAME */));
		holder.signal.setProgress(cursor.getInt(5 /* SIGNAL_STRENGTH */));
		holder.event.setImageLevel(cursor.getInt(6 /* EVENT */));

		final int action = cursor.getInt(7 /* ACTION */);
		switch (action) {
		default:
			holder.action.setImageResource(R.drawable.ic_action);
			holder.action.setImageLevel(action);
			break;
		}
		holder.name.setAlpha(enabled ? 1.0f : 0.5f);
		holder.event.setAlpha(enabled ? 1.0f : 0.5f);
		holder.action.setAlpha(enabled ? 1.0f : 0.5f);
	}

	private class ViewHolder {
		private TextView name;
		private ProgressBar signal;
		private ImageView event;
		private ImageView action;
	}

}
