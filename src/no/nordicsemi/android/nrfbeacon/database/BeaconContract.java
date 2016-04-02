/*******************************************************************************
 * Copyright (c) 2014 Nordic Semiconductor. All Rights Reserved.
 * 
 * The information contained herein is property of Nordic Semiconductor ASA.
 * Terms and conditions of usage are described in detail in NORDIC SEMICONDUCTOR STANDARD SOFTWARE LICENSE AGREEMENT.
 * Licensees are granted free, non-transferable use of the information. NO WARRANTY of ANY KIND is provided. 
 * This heading must NOT be removed from the file.
 ******************************************************************************/
package no.nordicsemi.android.nrfbeacon.database;

import android.provider.BaseColumns;

public class BeaconContract {
	public final static int EVENT_OUT_OF_RANGE = 0;
	public final static int EVENT_IN_RANGE = 1;
	public final static int EVENT_GET_NEAR = 2;
	public final static int EVENT_ON_TOUCH = 3;

	public final static int ACTION_MONA_LISA = 0;
	public final static int ACTION_ALARM = 3;
	public final static int ACTION_SILENT = 4;

	protected interface BeaconColumns {
		/** The user defined sensor name */
		public final static String NAME = "name";
		/** The beacon service uuid */
		public final static String UUID = "uuid";
		/** The beacon major number */
		public final static String MAJOR = "major";
		/** The beacon minor */
		public final static String MINOR = "minor";
		/** The last signal strength in percentage */
		public final static String SIGNAL_STRENGTH = "signal_strength";
		/** The event that triggers the action */
		public final static String EVENT = "event";
		/** The action assigned to the beacon */
		public final static String ACTION = "action";
		/** The optional parameter for the action (URL, application package, etc) */
		public final static String ACTION_PARAM = "action_param";
		/** 1 if beacon notifications are enabled, 0 if disabled */
		public final static String ENABLED = "enabled";
	}

	public final class Beacon implements BaseColumns, BeaconColumns {
		private Beacon() {
			// empty
		}
	}
}
