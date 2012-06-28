package com.octo.android.rest.client.contentservice;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import org.springframework.web.client.RestClientException;

import roboguice.service.RoboService;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.octo.android.rest.client.contentmanager.RestRequest;


/**
 * This is an abstract class used to manage the cache and provide web service result to an activity. <br/>
 * 
 * Extends this class to provide a service able to load content from web service or cache (if available and enabled)
 * 
 * @author jva
 */
public class ContentService extends RoboService {

	// ============================================================================================
	// CONSTANTS
	// ============================================================================================

	public static final int RESULT_OK = 0;
	public static final int RESULT_ERROR = -1;

	protected static final String FILE_CACHE_EXTENSION = ".store";
	private static final String LOGCAT_TAG = "AbstractContentService";


	// ============================================================================================
	// ATTRIBUTES
	// ============================================================================================
	
	private ContentServiceBinder mContentServiceBinder;

	// ============================================================================================
	// CONSTRUCTOR
	// ============================================================================================
	/**
	 * Basic constructor
	 * 
	 * @param name
	 */
	public ContentService() {
		mContentServiceBinder = new ContentServiceBinder();
	}

	// ============================================================================================
	// METHODS
	// ============================================================================================


	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void processRequest(RestRequest request, Bundle bundle, boolean isCacheEnabled, boolean isParallelizable) {

		Class<?> clazz = request.getResultType();
		Log.d(LOGCAT_TAG, "Result type is " + clazz.getName());

		Object result = null;
		String cacheKey = request.getCacheKey();
		Log.d(LOGCAT_TAG, "Loading content for key : " + cacheKey);
		String cacheFilename = cacheKey + FILE_CACHE_EXTENSION;


		if (isCacheEnabled) {
			Date lastModifiedDateForCache = null;

			// first check in the cache (file in private file system)
			File file = new File(this.getCacheDir(), cacheFilename);
			if (file.exists()) {
				lastModifiedDateForCache =  new Date(file.lastModified());
			}
			// if file is found, check the date : if the cache did not expired, return data
			if (lastModifiedDateForCache != null && isCacheExpired(new Date(), lastModifiedDateForCache) == false) {
				Log.d(LOGCAT_TAG,"Content available in cache and not expired");

				try {
					result = request.loadDataFromCache(cacheFilename);
				} catch (FileNotFoundException e) {
					Log.e(getClass().getName(),"Cache file cacheFilename not found:"+cacheFilename,e);
					return;
				} catch (IOException e) {
					e.printStackTrace();
					return;
				}
			}
		}

		if( result == null ) 
		{
			// if file is not found or the date is a day after or cache disabled, call the web service
			Log.d(LOGCAT_TAG,"Cache content not available or expired or disabled");
			if (!isNetworkAvailable(this)) {
				Log.e(LOGCAT_TAG,"Network is down.");
			}
			else {
				try {
					result =  request.loadDataFromNetwork();
				} catch (RestClientException e) {
					Log.e(LOGCAT_TAG,"A rest client exception occured during service execution :"+e.getMessage(), e);
				}

				if (result == null) {
					Log.e(LOGCAT_TAG,"Unable to get web service result : " + clazz );
				}
				else {
					if (isCacheEnabled) {
						try {
							Log.d(LOGCAT_TAG,"Start caching content...");
							result = request.saveDataToCacheAndReturnData(result, cacheFilename);
						} catch (FileNotFoundException e) {
							Log.e(LOGCAT_TAG,"A file not found exception occured during service execution :"+e.getMessage(), e);
						} catch (IOException e) {
							Log.e(LOGCAT_TAG,"An io exception occured during service execution :"+e.getMessage(), e);
						}
					}
				}
			}
		}

		// set result code
		int resultCode = RESULT_ERROR;
		if (result != null) {
			resultCode = RESULT_OK;
		}

		if( !request.isCanceled() ) {
			request.onRequestFinished(request.getRequestId(), resultCode, result);
		}
	}


	/**
	 * Check if the data in the cache is expired. To achieve that, check the last modified date of the file is today or not.<br/>
	 * If the file was modified a day or more before today, the cache is expired, otherwise the cache can be used
	 * 
	 * @param lastModifiedDate
	 * @return
	 */
	public boolean isCacheExpired(Date today, Date lastModifiedDate) {
		Calendar cal1 = Calendar.getInstance();
		Calendar cal2 = Calendar.getInstance();
		cal1.setTime(today);
		cal2.setTime(lastModifiedDate);

		boolean areDatesInSameDay = cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) && cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);

		return !areDatesInSameDay;
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



	@Override
	public IBinder onBind(Intent intent) {
		return mContentServiceBinder;
	}

	public class ContentServiceBinder extends Binder implements IContentService {
		public ContentService getContentService() {
			return ContentService.this;
		}
	}


}
