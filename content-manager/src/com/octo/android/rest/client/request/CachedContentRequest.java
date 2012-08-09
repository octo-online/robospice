package com.octo.android.rest.client.request;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.octo.android.rest.client.persistence.CacheExpiredException;
import com.octo.android.rest.client.persistence.DataPersistenceManager;

public abstract class CachedContentRequest<RESULT > extends ContentRequest<RESULT> {

	protected static final String FILE_CACHE_EXTENSION = ".cached";
	private static final String LOGCAT_TAG = "AbstractContentService";

	private DataPersistenceManager persistenceManager;
	private Context context;

	public CachedContentRequest(Context context, Class<RESULT> clazz, DataPersistenceManager dataPersistenceManager) {
		super(clazz);
		this.context = context;
		this.persistenceManager = dataPersistenceManager;
	}
	
	protected DataPersistenceManager getDataPersistenceManager() {
		return persistenceManager;
	}

	public abstract Object getCacheKey();
	
	/**
	 * Return the maximimum time a data can be cached before being considered expired.
	 * @return the maximimum time a data can be cached before being considered expired.
	 */
	public long getMaxTimeInCacheBeforeExpiry() {
		return 0;
	}


	public RESULT loadDataFromCache(Object cacheKey) throws FileNotFoundException,
	IOException, CacheExpiredException {
		return persistenceManager.getDataClassPersistenceManager(getResultType()).loadDataFromCache(cacheKey, getMaxTimeInCacheBeforeExpiry() );
	}

	public RESULT saveDataToCacheAndReturnData(RESULT data, Object cacheKey)
			throws FileNotFoundException, IOException {
		return persistenceManager.getDataClassPersistenceManager(getResultType()).saveDataToCacheAndReturnData(data, cacheKey);
	}

	public RESULT loadDataFromNetwork() throws Exception {
		return null;
	}
	
	protected File getCacheFile() {
		String cacheFilename = getCacheKey() + FILE_CACHE_EXTENSION;

		// first check in the cache (file in private file system)
		File file = new File(context.getCacheDir(), cacheFilename);
		return file;
	}

	@Override
	public final RESULT loadData() {

		RESULT result = null;

		try {
			result = loadDataFromCache(getCacheKey());
			if( result != null ) {
				return result;
			}
		} catch (FileNotFoundException e) {
			Log.d(getClass().getName(),"Cache file not found.",e);
		} catch (IOException e) {
			Log.d(getClass().getName(),"Cache file could not be read.",e);
		} catch (CacheExpiredException e) {
			Log.d(getClass().getName(),"Cache file has expired.",e);
		}


		// if file is not found or the date is a day after or cache disabled, call the web service
		Log.d(LOGCAT_TAG,"Cache content not available or expired or disabled");
		if (!isNetworkAvailable(context)) {
			Log.e(LOGCAT_TAG,"Network is down.");
			return null;
		}
		else {
			try {
				result =  loadDataFromNetwork();
				if (result == null) {
					Log.e(LOGCAT_TAG,"Unable to get web service result : " + getResultType() );
					return result;
				}
			} catch (Exception e) {
				Log.e(LOGCAT_TAG,"A rest client exception occured during service execution :"+e.getMessage(), e);
			}


			try {
				Log.d(LOGCAT_TAG,"Start caching content...");
				result = saveDataToCacheAndReturnData(result, getCacheKey());
				return result;
			} catch (FileNotFoundException e) {
				Log.e(LOGCAT_TAG,"A file not found exception occured during service execution :"+e.getMessage(), e);
			} catch (IOException e) {
				Log.e(LOGCAT_TAG,"An io exception occured during service execution :"+e.getMessage(), e);
			}
		}
		return null;
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
	 * Check if the data in the cache is expired. To achieve that, check the last modified date of the file is today or not.<br/>
	 * If the file was modified a day or more before today, the cache is expired, otherwise the cache can be used
	 * 
	 * @param lastModifiedDate
	 * @return
	 */
	public boolean isExpired(File file) {

		Date lastModifiedDate = new Date(file.lastModified());
		Calendar cal1 = Calendar.getInstance();
		Calendar cal2 = Calendar.getInstance();
		cal1.setTime(new Date());
		cal2.setTime(lastModifiedDate);

		boolean areDatesInSameDay = cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) && cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);

		return !areDatesInSameDay;
	}

}