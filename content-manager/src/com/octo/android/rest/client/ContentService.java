package com.octo.android.rest.client;

import java.util.Set;

import android.app.Application;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.octo.android.rest.client.persistence.CacheManager;
import com.octo.android.rest.client.request.CachedContentRequest;
import com.octo.android.rest.client.request.RequestListener;
import com.octo.android.rest.client.request.RequestProcessor;

/**
 * This is an abstract class used to manage the cache and provide web service result to an activity. <br/>
 * 
 * Extends this class to provide a service able to load content from web service or cache (if available and enabled)
 * 
 * @author jva & sni
 */
public abstract class ContentService extends Service {

	private final static String LOG_CAT = "ContentService";

	// ============================================================================================
	// ATTRIBUTES
	// ============================================================================================

	// ============================================================================================
	// ATTRIBUTES
	// ============================================================================================
	public ContentServiceBinder mContentServiceBinder;

	/** Responsible for persisting data. */
	private CacheManager cacheManager;

	private RequestProcessor requestProcessor;

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

	@Override
	public void onCreate() {
		super.onCreate();
		cacheManager = createCacheManager(getApplication());
		requestProcessor = new RequestProcessor(getApplicationContext(), cacheManager);

		Log.d(LOG_CAT, "Content Service instance created.");
	}

	public abstract CacheManager createCacheManager(Application application);

	// ============================================================================================
	// DELEGATE METHODS (to ease tests)
	// ============================================================================================

	public void addRequest(final CachedContentRequest<?> request, Set<RequestListener<?>> listRequestListener) {
		requestProcessor.addRequest(request, listRequestListener);
	}

	public boolean removeDataFromCache(Class<?> clazz, Object cacheKey) {
		return requestProcessor.removeDataFromCache(clazz, cacheKey);
	}

	public void removeAllDataFromCache(Class<?> clazz) {
		requestProcessor.removeAllDataFromCache(clazz);
	}

	public void removeAllDataFromCache() {
		requestProcessor.removeAllDataFromCache();
	}

	public boolean isFailOnCacheError() {
		return requestProcessor.isFailOnCacheError();
	}

	public void setFailOnCacheError(boolean failOnCacheError) {
		requestProcessor.setFailOnCacheError(failOnCacheError);
	}

	// ============================================================================================
	// SERVICE METHODS
	// ============================================================================================

	@Override
	public IBinder onBind(Intent intent) {
		return mContentServiceBinder;
	}

	public class ContentServiceBinder extends Binder {
		public ContentService getContentService() {
			return ContentService.this;
		}
	}
}