package com.octo.android.rest.client.contentservice;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import org.springframework.web.client.RestClientException;

import roboguice.service.RoboIntentService;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

import com.google.inject.Inject;
import com.octo.android.rest.client.contentmanager.ContentManager;
import com.octo.android.rest.client.contentmanager.RestRequest;
import com.octo.android.rest.client.contentservice.persistence.DataClassPersistenceManager;
import com.octo.android.rest.client.contentservice.persistence.DataPersistenceManager;
import com.octo.android.rest.client.utils.ContentRequestBundleUtils;
import com.octo.android.rest.client.webservice.WebService;


/**
 * This is an abstract class used to manage the cache and provide web service result to an activity. <br/>
 * 
 * Extends this class to provide a service able to load content from web service or cache (if available and enabled)
 * 
 * @author jva
 */
public class ContentService extends RoboIntentService {

	// ============================================================================================
	// CONSTANTS
	// ============================================================================================

	public static final int RESULT_OK = 0;
	public static final int RESULT_ERROR = -1;

	protected static final String FILE_CACHE_EXTENSION = ".store";
	public static final String BUNDLE_EXTRA_CACHE_ENABLED = "BUNDLE_EXTRA_CACHE_ENABLED";
	private static final String LOGCAT_TAG = "AbstractContentService";


	// ============================================================================================
	// ATTRIBUTES
	// ============================================================================================

	@Inject private WebService webService;

	@Inject
	private DataPersistenceManager dataPersistenceManager;

	// ============================================================================================
	// CONSTRUCTOR
	// ============================================================================================
	/**
	 * Basic constructor
	 * 
	 * @param name
	 */
	public ContentService() {
		super("AbstractContentService");
	}

	// ============================================================================================
	// METHODS
	// ============================================================================================

	@Override
	/**
	 * Try to get content from cache if enabled. If content is not available or expired, call webservice.
	 * 
	 * Cache is disabled by default, to enable it add <code>bundle.putBoolean(ContentService.BUNDLE_EXTRA_CACHE_ENABLED, true);</code> to the intent 
	 */
	protected void onHandleIntent(Intent intent) {
		
		//extract class of request from bundle
		RestRequest<?,?> request = (RestRequest<?,?>) intent.getSerializableExtra(ContentManager.INTENT_EXTRA_REST_REQUEST);
		Class<?> clazz = request.getResultType();
		Log.d(LOGCAT_TAG, "Result type is " + clazz.getName());

		Object result = null;
		Bundle bundle = intent.getParcelableExtra(ContentManager.INTENT_EXTRA_REST_REQUEST_BUNDLE);
		String cacheKey = request.getCacheKey();
		Log.d(LOGCAT_TAG, "Loading content for key : " + cacheKey);
		String cacheFilename = cacheKey + FILE_CACHE_EXTENSION;

		boolean isCacheEnabled = intent.getBooleanExtra(BUNDLE_EXTRA_CACHE_ENABLED, false);

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
					result = loadDataFromCache(clazz, cacheFilename);
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

			try {
				if (!isNetworkAvailable(this)) {
					Log.e(LOGCAT_TAG,"Network is down.");
				}
				else {
					result =  request.loadDataFromNetwork(webService, bundle );

					if (result == null) {
						Log.e(LOGCAT_TAG,"Unable to get web service result : " + clazz );
					}
					else {
						if (isCacheEnabled) {
							Log.d(LOGCAT_TAG,"Start caching content...");
							result = saveDataToCacheAndReturnData(result, cacheFilename);
						}
					}
				}
			}
			catch (RestClientException e) {
				Log.e(LOGCAT_TAG,"A rest client exception occured during service execution :"+e.getMessage(), e);
			} catch (FileNotFoundException e) {
				Log.e(LOGCAT_TAG,"A file not found exception occured during service execution :"+e.getMessage(), e);
			} catch (IOException e) {
				Log.e(LOGCAT_TAG,"An io exception occured during service execution :"+e.getMessage(), e);
			}
		}

		// set result code
		int resultCode = RESULT_ERROR;
		if (result != null) {
			resultCode = RESULT_OK;
		}

		// finally send the information back to the activity with a messenger
		sendBackResultToCaller(intent, result, resultCode);
	}

	/**
	 * Send retrieved data to caller
	 * 
	 * @param incomingIntent
	 *            initial intent that was sent to retrieve the content
	 * @param result
	 *            retrieved data object
	 * @param resultCode
	 *            RESULT_OK or RESULT_ERROR
	 */
	protected void sendBackResultToCaller(Intent incomingIntent, Object result, int resultCode) {
		Bundle extras = incomingIntent.getExtras();

		if (extras != null) {

			ResultReceiver resultReceiver = (ResultReceiver) extras.get(ContentManager.INTENT_EXTRA_RECEIVER);

			if (resultReceiver != null) {
				try {
					ContentRequestBundleUtils.setResultCodeAndObjectInBundle(extras, resultCode, result);
				}
				catch (IOException e) {
					Log.e(LOGCAT_TAG,"Unable to put service result into bundle before back send",e);
				}
				resultReceiver.send(resultCode, extras);
			}
			else {
				Log.v(LOGCAT_TAG,"Receiver should not be null");
			}
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
	 * Implement this method to get data from local cache system
	 * 
	 * @param cacheFileName
	 * @return the object result of the cache system
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	protected  <T> T loadDataFromCache(Class<T> clazz, String cacheFileName) throws FileNotFoundException, IOException {
		return dataPersistenceManager.getDataClassPersistenceManager(clazz).loadDataFromCache(clazz, cacheFileName);
	}

	/**
	 * Implement this method to save data in local cache system
	 * 
	 * @param data
	 *            data to save
	 * @param cacheFileName
	 *            name of the data in cache system
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	@SuppressWarnings("unchecked")
	protected <T> T saveDataToCacheAndReturnData(T data, String cacheFileName) throws FileNotFoundException, IOException {
		DataClassPersistenceManager<T> dataClassPersistenceManager = (DataClassPersistenceManager<T>) dataPersistenceManager.getDataClassPersistenceManager(data.getClass());
		return dataClassPersistenceManager.saveDataToCacheAndReturnData(data, cacheFileName);
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

}
