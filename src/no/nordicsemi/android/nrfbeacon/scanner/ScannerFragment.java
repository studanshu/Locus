/*******************************************************************************
 * Copyright (c) 2013 Nordic Semiconductor. All Rights Reserved.
 * 
 * The information contained herein is property of Nordic Semiconductor ASA.
 * Terms and conditions of usage are described in detail in NORDIC SEMICONDUCTOR STANDARD SOFTWARE LICENSE AGREEMENT.
 * Licensees are granted free, non-transferable use of the information. NO WARRANTY of ANY KIND is provided. 
 * This heading must NOT be removed from the file.
 ******************************************************************************/
package no.nordicsemi.android.nrfbeacon.scanner;

import java.util.UUID;

import com.vatsi.locus.R;
import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.ParcelUuid;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

/**
 * ScannerFragment class scan required BLE devices and shows them in a list. This class scans and filter devices with standard BLE Service UUID and devices with custom BLE Service UUID It contains a
 * list and a button to scan/cancel. There is a interface {@link OnDeviceSelectedListener} which is implemented by activity in order to receive selected device. The scanning will continue for 5
 * seconds and then stop
 */
public class ScannerFragment extends DialogFragment {
	private final static String TAG = "ScannerFragment";

	private final static String PARAM_UUID = "param_uuid";
	private final static long SCAN_DURATION = 5000;
	/* package */static final int NO_RSSI = -1000;

	private BluetoothAdapter mBluetoothAdapter;
	private DeviceListAdapter mAdapter;
	private Handler mHandler = new Handler();
	private Button mScanButton;

	private UUID mUuid;
	private boolean mIsScanning = false;

	/**
	 * Static implementation of fragment so that it keeps data when phone orientation is changed For standard BLE Service UUID, we can filter devices using normal android provided command
	 * startScanLe() with required BLE Service UUID For custom BLE Service UUID, we will use class ScannerServiceParser to filter out required device
	 */
	public static ScannerFragment getInstance(final Context context, final UUID uuid) {
		final ScannerFragment fragment = new ScannerFragment();

		final Bundle args = new Bundle();
		args.putParcelable(PARAM_UUID, new ParcelUuid(uuid));
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		final Bundle args = getArguments();
		final ParcelUuid pu = args.getParcelable(PARAM_UUID);
		mUuid = pu.getUuid();

		final BluetoothManager manager = (BluetoothManager) getActivity().getSystemService(Context.BLUETOOTH_SERVICE);
		mBluetoothAdapter = manager.getAdapter();
	}

	@Override
	public void onDestroyView() {
		stopScan();
		super.onDestroyView();
	}

	/**
	 * When dialog is created then set AlertDialog with list and button views
	 */
	@Override
	public Dialog onCreateDialog(final Bundle savedInstanceState) {
		final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		final View dialogView = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_scanner_device_selection, null);
		final ListView listview = (ListView) dialogView.findViewById(android.R.id.list);

		listview.setEmptyView(dialogView.findViewById(android.R.id.empty));
		listview.setAdapter(mAdapter = new DeviceListAdapter(getActivity()));

		builder.setTitle(R.string.scanner_title);
		final AlertDialog dialog = builder.setView(dialogView).create();
		listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id) {
				stopScan();
				dismiss();

				final ScannerFragmentListener listener = (ScannerFragmentListener) getParentFragment();
				final ExtendedBluetoothDevice device = (ExtendedBluetoothDevice) mAdapter.getItem(position);
				listener.onDeviceSelected(device.device, device.name);
			}
		});

		mScanButton = (Button) dialogView.findViewById(R.id.action_cancel);
		mScanButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (v.getId() == R.id.action_cancel) {
					if (mIsScanning) {
						dialog.cancel();
					} else {
						startScan();
					}
				}
			}
		});

		if (savedInstanceState == null)
			startScan();
		return dialog;
	}

	/**
	 * Scan for 5 seconds and then stop scanning when a BluetoothLE device is found then mLEScanCallback is activated This will perform regular scan for custom BLE Service UUID and then filter out
	 * using class ScannerServiceParser
	 */
	private void startScan() {
		mAdapter.clearDevices();
		mScanButton.setText(R.string.scanner_action_cancel);

		mBluetoothAdapter.startLeScan(mLEScanCallback);

		mIsScanning = true;
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				if (mIsScanning) {
					stopScan();
				}
			}
		}, SCAN_DURATION);
	}

	/**
	 * Stop scan if user tap Cancel button
	 */
	private void stopScan() {
		if (mIsScanning) {
			mScanButton.setText(R.string.scanner_action_scan);
			mBluetoothAdapter.stopLeScan(mLEScanCallback);
			mIsScanning = false;
		}
	}

	/**
	 * if scanned device already in the list then update it otherwise add as a new device
	 */
	private void addOrUpdateScannedDevice(final BluetoothDevice device, final String name, final int rssi) {
		getActivity().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				mAdapter.addOrUpdateDevice(new ExtendedBluetoothDevice(device, name, rssi));
			}
		});
	}

	/**
	 * Callback for scanned devices class {@link ScannerServiceParser} will be used to filter devices with custom BLE service UUID then the device will be added in a list
	 */
	private BluetoothAdapter.LeScanCallback mLEScanCallback = new BluetoothAdapter.LeScanCallback() {
		@Override
		public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
			if (device != null) {
				try {
					if (ScannerServiceParser.decodeDeviceAdvData(scanRecord, mUuid)) {
						// On some devices device.getName() is always null. We have to parse the name manually :(
						// This bug has been found on Sony Xperia Z1 (C6903) with Android 4.3.
						// https://devzone.nordicsemi.com/index.php/cannot-see-device-name-in-sony-z1
						addOrUpdateScannedDevice(device, ScannerServiceParser.decodeDeviceName(scanRecord), rssi);
					}
				} catch (Exception e) {
					Log.w(TAG, "Invalid data in Advertisement packet " + e.toString());
				}
			}
		}
	};
}
