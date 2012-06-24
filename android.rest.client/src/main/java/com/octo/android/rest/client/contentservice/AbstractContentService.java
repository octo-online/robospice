package com.octo.android.rest.client.contentservice;

import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import roboguice.service.RoboIntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

import com.google.inject.Inject;
import com.octo.android.rest.client.contentmanager.AbstractContentManager;
import com.octo.android.rest.client.contentmanager.RestRequest;
import com.octo.android.rest.client.contentservice.loader.JSonContentLoader;
import com.octo.android.rest.client.contentservice.loader.StringContentLoader;
import com.octo.android.rest.client.utils.CacheFileUtils;
import com.octo.android.rest.client.utils.ContentRequestBundleUtils;
import com.octo.android.rest.client.utils.DeviceUtils;
import com.octo.android.rest.client.webservice.WebService;


/**
 * This is an abstract class used to manage the cache and provide web service result to an activity. <br/>
 * 
 * Extends this class to provide a service able to load content from web service or cache (if available and enabled)
 * 
 * @author jva
 */
public class AbstractContentService extends RoboIntentService {

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
	
	@Inject private StringContentLoader stringContentLoader;
	
	@Inject private JSonContentLoader jSonContentLoader;

	@Inject private WebService webService;
	// ============================================================================================
	// CONSTRUCTOR
	// ============================================================================================
	/**
	 * Basic constructor
	 * 
	 * @param name
	 */
	public AbstractContentService() {
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
		RestRequest<?,?> request = (RestRequest<?,?>) intent.getSerializableExtra(AbstractContentManager.INTENT_EXTRA_REST_REQUEST);
		Class<?> clazz = request.getResultType();

		String cacheKey = request.getCacheKey();
		Log.d(LOGCAT_TAG, "Loading content for key : " + cacheKey);

		Object result = null;
		Date lastModifiedDateForCache = null;
		String cacheFilename = cacheKey + FILE_CACHE_EXTENSION;
		boolean isCacheEnabled = intent.getBooleanExtra(BUNDLE_EXTRA_CACHE_ENABLED, false);

		if (isCacheEnabled) {
			// first check in the cache (file in private file system)
			lastModifiedDateForCache = CacheFileUtils.getModifiedDateForFile(this, cacheFilename);
		}


		// if file is found, check the date : if the cache did not expired, return data
		if (lastModifiedDateForCache != null && isCacheExpired(new Date(), lastModifiedDateForCache) == false) {
			Log.d(LOGCAT_TAG,"Content available in cache and not expired");

			result = loadDataFromCache(clazz, cacheFilename);
		}
		else {
			// if file is not found or the date is a day after or cache disabled, call the web service
			Log.d(LOGCAT_TAG,"Cache content not available or expired or disabled");

			try {
				if (!DeviceUtils.isNetworkAvailable(this)) {
					Log.e(LOGCAT_TAG,"Network is down.");
				}
				else {
					result =  request.loadDataFromNetwork(webService);

					if (result == null) {
						Log.e(LOGCAT_TAG,"Unable to get web service result");
					}
					else {
						if (isCacheEnabled) {
							Log.d(LOGCAT_TAG,"Start caching content...");
							// callSaveDataInCacheAsyncAndReturnData method can be overriden by subclasses (for inputstreams)
							result = asyncSaveDataToCacheAndReturnData(result, cacheFilename);
						}
					}
				}
			}
			catch (Exception e) {
				Log.e(LOGCAT_TAG,"An exception occured during service execution :"+e.getMessage(), e);
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
	 * Default implementation of an async invocation of saveDataInCache. Simply invoke the method and return the parameter result, unchanged. This method will be overriden for some types of result
	 * (namely input streams)
	 * 
	 * @param result
	 *            the data to save in cache.
	 * @param cacheFilename
	 *            the name of the cache file to save the item in.
	 * @return the data that should be send back to service caller.
	 * @throws IOException
	 *             if some problem occurs during during creation of a new result in overriden methods.
	 */
	protected <T> T asyncSaveDataToCacheAndReturnData(final T result, final String cacheFilename) throws IOException {
		new CacheWritingThread(result, cacheFilename).start();
		return result;
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

			ResultReceiver resultReceiver = (ResultReceiver) extras.get(AbstractContentManager.INTENT_EXTRA_RECEIVER);

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

	protected String getCacheKey(Bundle extraBundle) {
		//TODO
		return "default cache key, change this ! AbstractContentService :: 198";
	}


	/**
	 * Implement this method to get data from local cache system
	 * 
	 * @param cacheFileName
	 * @return the object result of the cache system
	 */
	@SuppressWarnings("unchecked")
	protected  <T> T loadDataFromCache(Class<T> clazz, String cacheFileName) {
		if( clazz.equals(String.class)) {
			return (T) loadStringDataFromCache(cacheFileName);
		}
		else if( Arrays.asList(clazz.getInterfaces()).contains(Serializable.class) ) {
			Class<? extends Serializable> clazzSerializable = clazz.asSubclass(Serializable.class);
			return (T) loadSerializableDataFromCache(clazzSerializable, cacheFileName);
		}
		throw new IllegalArgumentException( "Class "+clazz.getName() + " is not handled by any registered loader" );
	}

	protected  String loadStringDataFromCache( String cacheFileName ) {
		return stringContentLoader.loadDataFromCache(cacheFileName);
	}

	protected <T extends Serializable> T loadSerializableDataFromCache( Class<T> clazz, String cacheFileName ) {
		return jSonContentLoader.loadDataFromCache(clazz, cacheFileName);
	}

	/**
	 * Implement this method to save data in local cache system
	 * 
	 * @param data
	 *            data to save
	 * @param cacheFileName
	 *            name of the data in cache system
	 */
	protected <T> void saveDataToCache(T data, String cacheFileName) {
		if( data instanceof String ) {
			stringContentLoader.saveDataToCache((String)data, cacheFileName);
			return;
		}
		else if( data instanceof Serializable) {
			jSonContentLoader.saveDataToCache((Serializable)data, cacheFileName);
			return;
		}
		throw new IllegalArgumentException( "Class "+data.getClass().getName() + " is not handled by any registered loader" );
	}

	// ============================================================================================
	// INNER CLASSES
	// ============================================================================================
	/**
	 * Thread which write result object as its json value in internal storage
	 * 
	 * @author mwa
	 * 
	 */
	protected class CacheWritingThread extends Thread {

		// attributes
		private final Object mResult;
		private final String mCacheFilename;

		public CacheWritingThread(Object result, String cacheFilename) {
			mCacheFilename = cacheFilename;
			mResult = result;
		}

		@Override
		public void run() {
			saveDataToCache(mResult, mCacheFilename);
		}
	}
}
