/*******************************************************************************
 * Copyright (c) 2014 Nordic Semiconductor. All Rights Reserved.
 * 
 * The information contained herein is property of Nordic Semiconductor ASA.
 * Terms and conditions of usage are described in detail in NORDIC SEMICONDUCTOR STANDARD SOFTWARE LICENSE AGREEMENT.
 * Licensees are granted free, non-transferable use of the information. NO WARRANTY of ANY KIND is provided. 
 * This heading must NOT be removed from the file.
 ******************************************************************************/
package no.nordicsemi.android.nrfbeacon.beacon;

import com.vatsi.locus.R;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class MonalisaFragment extends DialogFragment {

	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
		getDialog().setTitle(R.string.monalisa_title);
		return inflater.inflate(R.layout.fragment_dialog_monalisa, container, false);
	}

	@Override
	public void onCancel(final DialogInterface dialog) {
		super.onCancel(dialog);

		final BeaconsFragment targetFragment = (BeaconsFragment) getParentFragment();
		targetFragment.startScanning();
	}
}
