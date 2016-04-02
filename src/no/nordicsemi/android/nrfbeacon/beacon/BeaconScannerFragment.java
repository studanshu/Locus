/*******************************************************************************
 * Copyright (c) 2014 Nordic Semiconductor. All Rights Reserved.
 * 
 * The information contained herein is property of Nordic Semiconductor ASA.
 * Terms and conditions of usage are described in detail in NORDIC SEMICONDUCTOR STANDARD SOFTWARE LICENSE AGREEMENT.
 * Licensees are granted free, non-transferable use of the information. NO WARRANTY of ANY KIND is provided. 
 * This heading must NOT be removed from the file.
 ******************************************************************************/
package no.nordicsemi.android.nrfbeacon.beacon;

import no.nordicsemi.android.beacon.Beacon;
import no.nordicsemi.android.beacon.BeaconRegion;
import no.nordicsemi.android.beacon.BeaconServiceConnection;
import no.nordicsemi.android.beacon.Proximity;
import com.vatsi.locus.R;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class BeaconScannerFragment extends DialogFragment implements BeaconServiceConnection.BeaconsListener {
	private boolean mCompleted;

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mCompleted = false;
	}

	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_scan, container, false);
	}

	@Override
	public void onBeaconsInRegion(final Beacon[] beacons, final BeaconRegion region) {
		if (!mCompleted) {
			for (final Beacon beacon : beacons)
				if (Proximity.IMMEDIATE == beacon.getProximity()) {
					mCompleted = true;

					final BeaconsFragment parentFragment = (BeaconsFragment) getParentFragment();
					parentFragment.onScannerClosedWithResult(beacon);
					dismiss();
					break;
				}
		}
	}

	@Override
	public Dialog onCreateDialog(final Bundle savedInstanceState) {
		return new Dialog(getActivity(), R.style.AppTheme_Scanner);
	}

	@Override
	public void onCancel(final DialogInterface dialog) {
		super.onCancel(dialog);

		final BeaconsFragment targetFragment = (BeaconsFragment) getParentFragment();
		targetFragment.onScannerClosed();
	}
}
