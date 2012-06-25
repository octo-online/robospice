package com.octo.android.rest.client.contentservice.loader;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import android.app.Application;
import android.util.Log;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.octo.android.rest.client.utils.CacheFileUtils;

@Singleton
public final class JSonContentLoader<T  extends Serializable> extends DataContentLoader<T> {

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

	public final T loadDataFromCache(Class<T> clazz, String cacheFileName) throws JsonParseException, JsonMappingException, IOException {
		T result = null;
		String resultJson = null;
		resultJson = CacheFileUtils.readStringContentFromFile(mApplication, cacheFileName);

		if (resultJson != null) {
			// finally transform json in object
			if (StringUtils.isNotEmpty(resultJson)) {
				result = mJsonMapper.readValue(resultJson, clazz);
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

	@Override
	public T saveDataToCacheAndReturnData(T data, String cacheFileName)
			throws FileNotFoundException, IOException {
		String resultJson = null;

		// transform the content in json to store it in the cache
		resultJson = mJsonMapper.writeValueAsString(data);

		// finally store the json in the cache
		if (StringUtils.isNotEmpty(resultJson)) {
			CacheFileUtils.saveStringToFile(mApplication, resultJson, cacheFileName);
		}
		else {
			Log.e(getClass().getName(),"Unable to save web service result into the cache");
		}
		return data;
	}

	@Override
	public boolean canHandleData(Class<?> clazz) {
		try {
			clazz.asSubclass(Serializable.class);
			return true;
		}
		catch( ClassCastException ex ) {
			return false;
		}
	}

}
