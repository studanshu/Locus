/*******************************************************************************
 * Copyright (c) 2014 Nordic Semiconductor. All Rights Reserved.
 * 
 * The information contained herein is property of Nordic Semiconductor ASA.
 * Terms and conditions of usage are described in detail in NORDIC SEMICONDUCTOR STANDARD SOFTWARE LICENSE AGREEMENT.
 * Licensees are granted free, non-transferable use of the information. NO WARRANTY of ANY KIND is provided. 
 * This heading must NOT be removed from the file.
 ******************************************************************************/
package no.nordicsemi.android.nrfbeacon.beacon;

import java.util.UUID;

//import net.dinglisch.android.tasker.TaskerIntent;
import no.nordicsemi.android.beacon.Beacon;
import no.nordicsemi.android.beacon.BeaconRegion;
import no.nordicsemi.android.beacon.BeaconServiceConnection;
import no.nordicsemi.android.beacon.Proximity;
import com.vatsi.locus.R;
import no.nordicsemi.android.nrfbeacon.beacon.adapter.BeaconAdapter;
import no.nordicsemi.android.nrfbeacon.database.BeaconContract;
import no.nordicsemi.android.nrfbeacon.database.DatabaseHelper;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

public class BeaconsListFragment extends ListFragment implements BeaconServiceConnection.BeaconsListener, BeaconServiceConnection.RegionListener {
	private BeaconsFragment mParentFragment;
	private DatabaseHelper mDatabaseHelper;
	private BeaconAdapter mAdapter;

	@Override
	public void onActivityCreated(final Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		mParentFragment = (BeaconsFragment) getParentFragment();
		mDatabaseHelper = mParentFragment.getDatabaseHelper();
	}

	@Override
	public void onStart() {
		super.onStart();

		final Cursor cursor = mDatabaseHelper.getAllRegions();
		setListAdapter(mAdapter = new BeaconAdapter(getActivity(), cursor));
	}

	@Override
	public void onResume() {
		super.onResume();
		mParentFragment.startScanning();
	}

	@Override
	public void onPause() {
		super.onPause();
		mParentFragment.stopScanning();
	}

	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_beacons_list, container, false);
	}

	@Override
	public void onListItemClick(final ListView l, final View v, final int position, final long id) {
		mParentFragment.onEditRegion(id);
	}

	/**
	 * Registers for monitoring and ranging events for all regions in the database.
	 * 
	 * @param serviceConnection
	 *            the service connection used to bing activity to the service
	 */
	public void startScanning(final BeaconServiceConnection serviceConnection) {
		final Cursor cursor = mDatabaseHelper.getAllRegions();
		while (cursor.moveToNext()) {
			final UUID uuid = UUID.fromString(cursor.getString(2 /* UUID */));
			final int major = cursor.getInt(3 /* MAJOR */);
			final int minor = cursor.getInt(4 /* MINOR */);
			final int event = cursor.getInt(6 /* EVENT */);

			// We must start ranging for all beacons
			serviceConnection.startRangingBeaconsInRegion(uuid, major, minor, this);
			// And additionally start monitoring only for those with these two events set
			if (event == BeaconContract.EVENT_IN_RANGE || event == BeaconContract.EVENT_OUT_OF_RANGE)
				serviceConnection.startMonitoringForRegion(uuid, major, minor, this);
		}
	}

	/**
	 * Unregisters the fragment from receiving monitoring and ranging events.
	 * 
	 * @param serviceConnection
	 *            the service connection used to bind activity with the beacon service
	 */
	public void stopScanning(final BeaconServiceConnection serviceConnection) {
		if (serviceConnection != null) {
			serviceConnection.stopMonitoringForRegion(this);
			serviceConnection.stopRangingBeaconsInRegion(this);
		}
	}

	@Override
	public void onBeaconsInRegion(final Beacon[] beacons, final BeaconRegion region) {
		if (beacons.length > 0) {
			final Cursor cursor = mDatabaseHelper.findRegion(region);
			try {
				if (cursor.moveToNext()) {
					// Check and fire events
					final int event = cursor.getInt(6 /* EVENT */);
					for (final Beacon beacon : beacons) {
						if (event == BeaconContract.EVENT_ON_TOUCH && Proximity.IMMEDIATE.equals(beacon.getProximity()) && Proximity.NEAR.equals(beacon.getPreviousProximity())) {
							fireEvent(cursor);
							break;
						}
						if (event == BeaconContract.EVENT_GET_NEAR && Proximity.NEAR.equals(beacon.getProximity()) && Proximity.FAR.equals(beacon.getPreviousProximity())) {
							fireEvent(cursor);
							break;
						}
					}

					// Update signal strength in the database
					float accuracy = 5;
					for (final Beacon beacon : beacons)
						if (Proximity.UNKNOWN != beacon.getProximity() && beacon.getAccuracy() < accuracy)
							accuracy = beacon.getAccuracy();
					accuracy = -20 * accuracy + 100;
					mDatabaseHelper.updateRegionSignalStrength(cursor.getLong(0 /* _ID */), (int) accuracy);
				}
			} finally {
				cursor.close();
			}
			mAdapter.swapCursor(mDatabaseHelper.getAllRegions());
		}
	}

	@Override
	public void onEnterRegion(final BeaconRegion region) {
		final Cursor cursor = mDatabaseHelper.findRegion(region);
		try {
			if (cursor.moveToNext()) {
				final int event = cursor.getInt(6 /* EVENT */);
				if (event == BeaconContract.EVENT_IN_RANGE) {
					fireEvent(cursor);
				}
			}
		} finally {
			cursor.close();
		}
	}

	@Override
	public void onExitRegion(final BeaconRegion region) {
		final Cursor cursor = mDatabaseHelper.findRegion(region);
		try {
			if (cursor.moveToNext()) {
				final int event = cursor.getInt(6 /* EVENT */);
				if (event == BeaconContract.EVENT_OUT_OF_RANGE) {
					fireEvent(cursor);
				}
			}
		} finally {
			cursor.close();
		}
	}

	/**
	 * Fires the event associated with the region at the current position of the cursor.
	 * 
	 * @param cursor
	 *            the cursor with a region details obtained by f.e. {@link DatabaseHelper#findRegion(BeaconRegion)}. The cursor has to be moved to the proper position.
	 */
	private void fireEvent(final Cursor cursor) {
		final boolean enabled = cursor.getInt(9 /* ENABLED */) == 1;
		if (!enabled)
			return;

		final int action = cursor.getInt(7 /* ACTION */);
		final String actionParam = cursor.getString(8 /* ACTION PARAM */);

		switch (action) {
		case BeaconContract.ACTION_MONA_LISA: {
			mParentFragment.stopScanning();
			final DialogFragment dialog = new MonalisaFragment();
			dialog.show(mParentFragment.getChildFragmentManager(), "monalisa");
			break;
		}
		case BeaconContract.ACTION_SILENT: {
			final AudioManager audioManager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
			audioManager.setStreamVolume(AudioManager.STREAM_RING, 0, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE | AudioManager.FLAG_SHOW_UI | AudioManager.FLAG_ALLOW_RINGER_MODES);
			audioManager.setStreamVolume(AudioManager.STREAM_NOTIFICATION, 0, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
			break;
		}
		case BeaconContract.ACTION_ALARM: {
			final Uri alarm = RingtoneManager.getActualDefaultRingtoneUri(getActivity(), RingtoneManager.TYPE_ALARM);
			final Notification notification = new Notification.Builder(getActivity()).setContentTitle(getString(R.string.alarm_notification_title))
					.setContentText(getString(R.string.alarm_notification_message, cursor.getString(1 /* NAME */))).setSmallIcon(R.drawable.stat_sys_nrf_beacon).setAutoCancel(true)
					.setOnlyAlertOnce(true).setSound(alarm, AudioManager.STREAM_ALARM).build();
			final NotificationManager notificationManager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
			notificationManager.notify(1, notification);
			break;
		}
		}
	}
}
