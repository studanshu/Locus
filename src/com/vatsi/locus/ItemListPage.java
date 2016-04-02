package com.vatsi.locus;
import no.nordicsemi.android.nrfbeacon.beacon.BeaconsFragment;

import com.google.samples.apps.iosched.ui.widget.FloatingActionButton;
import com.vatsi.library.CodeLearnAdapter;
import com.vatsi.library.DatabaseHandler;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

public class ItemListPage extends ActionBarActivity{
	public static final String NRF_BEACON_SERVICE_URL = "market://details?id=no.nordicsemi.android.beacon.service";
	public static final String OPENED_FROM_LAUNCHER = "no.nordicsemi.android.nrtbeacon.extra.opened_from_launcher";
	private static final int REQUEST_ENABLE_BT = 1;

	private BeaconsFragment mBeaconsFragment;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.allitems);
		DatabaseHandler db=new DatabaseHandler(this);
		System.out.println("itemlistpage");
		CodeLearnAdapter chapterListAdapter = new CodeLearnAdapter(db);
		System.out.println("itemlistpage");
		ListView codeLearnLessons = (ListView)findViewById(R.id.listView1);
		codeLearnLessons.setAdapter(chapterListAdapter);
		final FloatingActionButton fabAdd = (FloatingActionButton) findViewById(R.id.fab_add);
		fabAdd.setCheckable(false);
		fabAdd.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(final View v) {
				if (mBeaconsFragment != null)
					mBeaconsFragment.onAddOrEditRegion();
			}
		});
		
		final ViewPager pager = (ViewPager) findViewById(R.id.view_pager);
		pager.setOffscreenPageLimit(2);
		pager.setAdapter(new FragmentAdapter(getSupportFragmentManager()));
	}

	@Override
	public boolean onOptionsItemSelected(final MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			onBackPressed();
			return true;
		}
		return false;
	}

	@Override
	protected void onResume() {
		super.onResume();

		if (!isBleEnabled())
			enableBle();

		// we are in main fragment, show 'home up' if entered from Launcher (splash screen activity)
		//final boolean openedFromLauncher = getIntent().getBooleanExtra(MainActivity.OPENED_FROM_LAUNCHER, false);
		//cmnt

	}

	@Override
	protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
		switch (requestCode) {
		case REQUEST_ENABLE_BT:
			if (resultCode == RESULT_OK) {
				// empty?
			} else
				System.out.println("OnActivityResult");//finish();
			break;
		default:
			super.onActivityResult(requestCode, resultCode, data);
		}
	}

	public void setBeaconsFragment(final BeaconsFragment fragment) {
		if (fragment == null && mBeaconsFragment != null)
			mBeaconsFragment.onFragmentPaused();
		if (fragment != null)
			fragment.onFragmentResumed();
		mBeaconsFragment = fragment;
	}

	/**
	 * Checks whether the device supports Bluetooth Low Energy communication
	 * 
	 * @return <code>true</code> if BLE is supported, <code>false</code> otherwise
	 */
	private boolean ensureBleExists() {
		if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
			Toast.makeText(this, R.string.no_ble, Toast.LENGTH_LONG).show();
			return false;
		}
		return true;
	}

	/**
	 * Checks whether the Bluetooth adapter is enabled.
	 */
	private boolean isBleEnabled() {
		final BluetoothManager bm = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
		final BluetoothAdapter ba = bm.getAdapter();
		return ba != null && ba.isEnabled();//*/
		//return true;
	}

	/**
	 * Tries to start Bluetooth adapter.
	 */
	private void enableBle() {
		final Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
		startActivityForResult(enableIntent, REQUEST_ENABLE_BT);//*/
	}

	private class FragmentAdapter extends FragmentPagerAdapter {

		public FragmentAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			switch (position) {
			default:
			case 0:
				return new BeaconsFragment();
			}
		}

		@Override
		public int getCount() {
			return 1;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return getResources().getStringArray(R.array.tab_title)[position];
		}

	}
}