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
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;

/**
 * Provides the list of all applications with {@link Intent#CATEGORY_LAUNCHER} category.
 */
public class ApplicationAdapter extends BaseAdapter {
	private final PackageManager mPackageManager;
	private final LayoutInflater mInflater;
	private final List<ResolveInfo> mApplications;
	private String mSelectedAppPackage;

	public ApplicationAdapter(final Context context, final String selectedAppPackage) {
		mPackageManager = context.getPackageManager();
		mInflater = LayoutInflater.from(context);
		mSelectedAppPackage = selectedAppPackage;

		final Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
		mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
		mApplications = context.getPackageManager().queryIntentActivities(mainIntent, 0);
	}

	@Override
	public int getCount() {
		return mApplications.size();
	}

	@Override
	public Object getItem(int position) {
		return mApplications.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		CheckedTextView view = (CheckedTextView) convertView;
		if (view == null) {
			view = (CheckedTextView) mInflater.inflate(R.layout.icon_list_item, parent, false);
		}
		final ResolveInfo info = (ResolveInfo) getItem(position);
		view.setText(info.loadLabel(mPackageManager));
		view.setCompoundDrawablesWithIntrinsicBounds(info.loadIcon(mPackageManager), null, null, null);
		view.setChecked(mSelectedAppPackage.equals(info.activityInfo.applicationInfo.packageName));
		return view;
	}
}
