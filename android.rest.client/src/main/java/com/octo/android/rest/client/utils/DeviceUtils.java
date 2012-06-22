package com.octo.android.rest.client.utils;

import java.util.UUID;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.DisplayMetrics;

public class DeviceUtils {
	// ============================================================================================
	// CONSTANTS
	// ============================================================================================

	private static final String PREFERENCE_UUID = "uuid";
	public static final int DEFAULT_DPI = 160;
	public static final int LDPI = 0;
	public static final int MDPI = 1;
	public static final int HDPI = 2;
	public static final int XHDPI = 3;

	// ============================================================================================
	// CONSTRUCTORS
	// ============================================================================================

	/**
	 * Prevent instantiation of utility class.
	 */
	private DeviceUtils() {
	}

	// ============================================================================================
	// METHODS
	// ============================================================================================

	/**
	 * 
	 * @param context
	 * @return screen dpi
	 */
	public static int getDensityDpi(Context context) {
		return context.getResources().getDisplayMetrics().densityDpi;
	}

	/**
	 * @param context
	 * @return LDPI, MDPI, HDPI or XHDPI switch device screen
	 */
	public static int getDensityCategory(Context context) {
		int screenDensityDpi = DeviceUtils.getDensityDpi(context);

		switch (screenDensityDpi) {
			case DisplayMetrics.DENSITY_LOW:
				return DeviceUtils.LDPI;

			case DisplayMetrics.DENSITY_MEDIUM:
				return DeviceUtils.MDPI;

			case DisplayMetrics.DENSITY_HIGH:
				return DeviceUtils.HDPI;

			default:
				if (screenDensityDpi > DisplayMetrics.DENSITY_HIGH) {
					return DeviceUtils.XHDPI;
				}
				return DeviceUtils.MDPI;
		}
	}

	public static int getScreenHeight(Context context) {
		return context.getResources().getDisplayMetrics().heightPixels;
	}

	public static int getScreenWidth(Context context) {
		return context.getResources().getDisplayMetrics().widthPixels;
	}

	/**
	 * @return true if network is available (at least one way to connect to network is connected or connecting).
	 */
	public static boolean isNetworkAvailable(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo[] allNetworkInfos = connectivityManager.getAllNetworkInfo();
		for (NetworkInfo networkInfo : allNetworkInfos) {
			if (networkInfo.getState() == NetworkInfo.State.CONNECTED || networkInfo.getState() == NetworkInfo.State.CONNECTING) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @return the uuid of the device. Will be stored for further use.
	 */
	public static String getDeviceId(Context context) {
		String id = context.getSharedPreferences(PREFERENCE_UUID, Context.MODE_PRIVATE).getString(PREFERENCE_UUID, null);
		if (id == null) {
			id = UUID.randomUUID().toString();
			context.getSharedPreferences(PREFERENCE_UUID, Context.MODE_PRIVATE).edit().putString(PREFERENCE_UUID, id).commit();
		}
		return id;
	}
}
