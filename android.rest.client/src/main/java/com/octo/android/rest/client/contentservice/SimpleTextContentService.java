package com.octo.android.rest.client.contentservice;

import java.io.IOException;

import android.util.Log;

import com.octo.android.rest.client.utils.CacheFileUtils;

/**
 * This is an abstract class used to manage the cache and provide web service result to an activity. <br/>
 * 
 * Extends this class to provide a service able to load content from web service or cache (if available and enabled)
 * 
 * @author sni
 * 
 */
public abstract class SimpleTextContentService extends AbstractContentService<String> {

	// ============================================================================================
	// CONSTRUCTOR
	// ============================================================================================

	public SimpleTextContentService(String name) {
		super(name);
	}

	// ============================================================================================
	// METHODS
	// ============================================================================================

	@Override
	protected final String loadDataFromCache(String cacheFileName) {
		return CacheFileUtils.readStringContentFromFile(this, cacheFileName);
	}

	@Override
	protected final void saveDataToCache(String data, String cacheFileName) {
		try {
			CacheFileUtils.saveStringToFile(this, data, cacheFileName);
		}
		catch (IOException e) {
			Log.e(getClass().getName(),"Unable to save web service result into the cache",e);
		}
	}
}
