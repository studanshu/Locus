/*******************************************************************************
 * Copyright (c) 2013 Nordic Semiconductor. All Rights Reserved.
 * 
 * The information contained herein is property of Nordic Semiconductor ASA. Terms and conditions of usage are described in detail in NORDIC SEMICONDUCTOR STANDARD SOFTWARE LICENSE AGREEMENT.
 * Licensees are granted free, non-transferable use of the information. NO WARRANTY of ANY KIND is provided. This heading must NOT be removed from the file.
 ******************************************************************************/
package no.nordicsemi.android.nrfbeacon.scanner;

import android.bluetooth.BluetoothDevice;

public class ExtendedBluetoothDevice {
	public BluetoothDevice device;
	public int rssi;
	public String name;

	public ExtendedBluetoothDevice(final BluetoothDevice device, final String name, final int rssi) {
		this.device = device;
		this.name = name;
		this.rssi = rssi;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof ExtendedBluetoothDevice) {
			final ExtendedBluetoothDevice that = (ExtendedBluetoothDevice) o;
			return device.getAddress().equals(that.device.getAddress());
		}
		return super.equals(o);
	}
}
