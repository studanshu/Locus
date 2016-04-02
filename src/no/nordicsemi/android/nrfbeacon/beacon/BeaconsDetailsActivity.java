/*******************************************************************************
 * Copyright (c) 2014 Nordic Semiconductor. All Rights Reserved.
 * 
 * The information contained herein is property of Nordic Semiconductor ASA.
 * Terms and conditions of usage are described in detail in NORDIC SEMICONDUCTOR STANDARD SOFTWARE LICENSE AGREEMENT.
 * Licensees are granted free, non-transferable use of the information. NO WARRANTY of ANY KIND is provided. 
 * This heading must NOT be removed from the file.
 ******************************************************************************/
package no.nordicsemi.android.nrfbeacon.beacon;

import java.util.List;

//import net.dinglisch.android.tasker.TaskerIntent;
import com.vatsi.locus.R;
import no.nordicsemi.android.nrfbeacon.beacon.adapter.ApplicationAdapter;
import no.nordicsemi.android.nrfbeacon.beacon.adapter.ResourceAdapter;
import no.nordicsemi.android.nrfbeacon.database.BeaconContract;
import no.nordicsemi.android.nrfbeacon.database.DatabaseHelper;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SwitchCompat;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

public class BeaconsDetailsActivity extends ActionBarActivity /*implements DownloadTaskerFragment.DownloadCanceledListener*/{
	private static final String DEFAULT_URL = "http://www.nordicsemi.no";
	private static final String DEFAULT_APP = "com.google.android.gm";

	public static final String ID = "id";
	//private static final int TASKER_REQUEST_CODE = 12335; // random number

	private DatabaseHelper mDatabaseHelper;

	private EditText mNameView;
	private TextView mUuidView;
	private TextView mMajorView;
	private TextView mMinorView;
	private TextView mEventView;
	private TextView mActionView;
	private View mActionParamContainer;
	private TextView mActionParamTitleView;
	private TextView mActionParamView;
	private SwitchCompat mEnableSwitch;

	private int mEvent;
	private int mAction;
	private String mActionParam;

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mDatabaseHelper = new DatabaseHelper(this);

		// Get the region id in database
		final Intent intent = getIntent();
		final long id = intent.getLongExtra(ID, 0);

		// Create and fill views
		createView(id);
		fillView(id);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
	}

	private void createView(final long id) {
		setContentView(R.layout.fragment_details);

		mUuidView = (TextView) findViewById(R.id.uuid);
		mMajorView = (TextView) findViewById(R.id.major);
		mMinorView = (TextView) findViewById(R.id.minor);
		mNameView = (EditText) findViewById(R.id.name);
		mEventView = (TextView) findViewById(R.id.event);
		mActionView = (TextView) findViewById(R.id.action);
		mActionParamContainer = findViewById(R.id.action_param_container);
		mActionParamTitleView = (TextView) findViewById(R.id.action_param_title);
		mActionParamView = (TextView) findViewById(R.id.action_param);
		mEnableSwitch = (SwitchCompat) findViewById(R.id.enable);

		findViewById(R.id.event_container).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(final View v) {
				final ResourceAdapter adapter = new ResourceAdapter(BeaconsDetailsActivity.this, R.array.settings_events, R.drawable.ic_event);
				new AlertDialog.Builder(BeaconsDetailsActivity.this).setTitle(R.string.settings_event).setSingleChoiceItems(adapter, mEvent, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(final DialogInterface dialog, final int which) {
						dialog.dismiss();
						mEvent = which;
						mEventView.setText(BeaconsDetailsActivity.this.getResources().getStringArray(R.array.settings_events)[which]);
						mEventView.getCompoundDrawables()[2].setLevel(which);
						mDatabaseHelper.updateRegionEvent(id, which);
					}
				}).setNegativeButton(R.string.cancel, null).show();
			}
		});

		findViewById(R.id.action_container).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(final View v) {
				final ResourceAdapter adapter = new ResourceAdapter(BeaconsDetailsActivity.this, R.array.settings_actions, R.drawable.ic_action);
				new AlertDialog.Builder(BeaconsDetailsActivity.this).setTitle(R.string.settings_action).setSingleChoiceItems(adapter, mAction, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(final DialogInterface dialog, final int which) {
						dialog.dismiss();
						final int previous = mAction;
						mAction = which;
						mActionView.setText(BeaconsDetailsActivity.this.getResources().getStringArray(R.array.settings_actions)[which]);
						mActionView.getCompoundDrawables()[2].setLevel(which);
						mDatabaseHelper.updateRegionAction(id, which);

						if (mAction != previous) {
							mActionParam = null;
							updateActionParam(which);
							mDatabaseHelper.updateRegionActionParam(id, mActionParam);
							performSetActionParam();
						}
					}
				}).setNegativeButton(R.string.cancel, null).show();
			}
		});

		findViewById(R.id.action_param_container).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(final View v) {
				performSetActionParam();
			}
		});

		mEnableSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(final CompoundButton buttonView, final boolean isChecked) {
				mDatabaseHelper.setRegionEnabled(id, isChecked);
			}
		});
	}

	private void fillView(final long id) {
		final Cursor cursor = mDatabaseHelper.getRegion(id);
		try {
			if (cursor.moveToNext()) {
				mNameView.setText(cursor.getString(1 /* NAME */));
				mUuidView.setText(cursor.getString(2 /* UUID */));
				mMajorView.setText(cursor.getString(3 /* MAJOR */));
				mMinorView.setText(cursor.getString(4 /* MINOR */));

				final int event = mEvent = cursor.getInt(6 /* EVENT */);
				mEventView.getCompoundDrawables()[2].setLevel(event);
				mEventView.setText(getResources().getStringArray(R.array.settings_events)[event]);

				final int action = mAction = cursor.getInt(7 /* ACTION */);
				mActionView.getCompoundDrawables()[2].setLevel(action);
				mActionView.setText(getResources().getStringArray(R.array.settings_actions)[action]);

				mActionParam = cursor.getString(8 /* ACTION_PARAM */);
				mEnableSwitch.setChecked(cursor.getInt(9 /* ENABLED */) == 1);
				updateActionParam(action);
			}
		} finally {
			cursor.close();
		}
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(0, 0);
	}

	@Override
	public void onStop() {
		super.onStop();

		// We must save the device name here
		final Intent intent = getIntent();
		final long id = intent.getLongExtra(ID, 0);

		final String newName = mNameView.getText().toString();
		if (!TextUtils.isEmpty(newName)) {
			mDatabaseHelper.updateRegionName(id, newName);

			// Hide the keyboard
			final InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(mNameView.getWindowToken(), 0);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.region_details, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(final MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_remove:
			final Intent intent = getIntent();
			final long id = intent.getLongExtra(ID, 0);
			mDatabaseHelper.deleteRegion(id);
			// no break
		case android.R.id.home:
			onBackPressed();
			return true;
		}
		return false;
	}

	private void updateActionParam(final int action) {
		switch (action) {
		
		default:
			mActionParamView.setText(null);
			mActionParamContainer.setVisibility(View.GONE);
			break;
		}
	}

	private void performSetActionParam() {
		final Intent intent = getIntent();
		final long id = intent.getLongExtra(ID, 0);
	}
}
