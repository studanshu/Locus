/*******************************************************************************
 * Copyright (c) 2014 Nordic Semiconductor. All Rights Reserved.
 * 
 * The information contained herein is property of Nordic Semiconductor ASA.
 * Terms and conditions of usage are described in detail in NORDIC SEMICONDUCTOR STANDARD SOFTWARE LICENSE AGREEMENT.
 * Licensees are granted free, non-transferable use of the information. NO WARRANTY of ANY KIND is provided. 
 * This heading must NOT be removed from the file.
 ******************************************************************************/
package no.nordicsemi.android.nrfbeacon.beacon.adapter;

import com.vatsi.locus.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;

public class ResourceAdapter extends BaseAdapter {
	private final LayoutInflater mInflater;
	private final String[] mEntity;
	private final int mIcon;

	public ResourceAdapter(final Context context, final int entitiesResId, final int iconLevelList) {
		mInflater = LayoutInflater.from(context);
		mEntity = context.getResources().getStringArray(entitiesResId);
		mIcon = iconLevelList;
	}

	@Override
	public int getCount() {
		return mEntity.length;
	}

	@Override
	public Object getItem(final int position) {
		return mEntity[position];
	}

	@Override
	public long getItemId(final int position) {
		return position;
	}

	@Override
	public View getView(final int position, final View convertView, final ViewGroup parent) {
		CheckedTextView view = (CheckedTextView) convertView;
		if (view == null) {
			view = (CheckedTextView) mInflater.inflate(R.layout.icon_list_item, parent, false);

		}
		view.setText(mEntity[position]);
		view.setCompoundDrawablesWithIntrinsicBounds(mIcon, 0, 0, 0);
		view.getCompoundDrawables()[0].setLevel(position);
		return view;
	}

}
