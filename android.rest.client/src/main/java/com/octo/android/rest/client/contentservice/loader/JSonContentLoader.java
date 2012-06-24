package com.octo.android.rest.client.contentservice.loader;

import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;

import android.app.Application;
import android.util.Log;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.octo.android.rest.client.utils.CacheFileUtils;

@Singleton
public final class JSonContentLoader {

	@Inject Application mApplication;
	
	// ============================================================================================
	// ATTRIBUTES
	// ============================================================================================

	private final ObjectMapper mJsonMapper;

	// ============================================================================================
	// CONSTRUCTOR
	// ============================================================================================

	public JSonContentLoader() {
		this.mJsonMapper = new ObjectMapper();
	}

	// ============================================================================================
	// METHODS
	// ============================================================================================

	public final <T extends Serializable> T loadDataFromCache(Class<T> clazz, String cacheFileName) {
		T result = null;
		String resultJson = null;
		resultJson = CacheFileUtils.readStringContentFromFile(mApplication, cacheFileName);

		if (resultJson != null) {
			// finally transform json in object
			if (StringUtils.isNotEmpty(resultJson)) {
				try {
					result = mJsonMapper.readValue(resultJson, clazz);
				}
				catch (IOException e) {
					Log.e(getClass().getName(),"Unable to restore cache content in an object of type " + clazz);
				}
			}
			else {
				Log.e(getClass().getName(),"Unable to restore cache content : cache file is empty");
			}
		}
		else {
			Log.e(getClass().getName(),"Unable to restore cache content");
		}
		return result;
	}

	public final void saveDataToCache(Serializable data, String cacheFileName) {
		String resultJson = null;

		// transform the content in json to store it in the cache
		try {
			resultJson = mJsonMapper.writeValueAsString(data);

			// finally store the json in the cache
			if (StringUtils.isNotEmpty(resultJson)) {
				CacheFileUtils.saveStringToFile(mApplication, resultJson, cacheFileName);
			}
			else {
				Log.e(getClass().getName(),"Unable to save web service result into the cache");
			}
		}
		catch (IOException e) {
			Log.e(getClass().getName(),"Unable to save web service result into the cache");
		}
	}

}
