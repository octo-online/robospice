package com.octo.android.rest.client.contentservice;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.ParameterizedType;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;

import android.os.Bundle;
import android.util.Log;

import com.octo.android.rest.client.utils.CacheFileUtils;

/**
 * This is an abstract class used to manage the cache and provide web service result to an activity. <br/>
 * 
 * Extends this class to provide a service able to load content from web service or cache (if available and enabled)
 * 
 * @author jva
 * 
 * @param <T>
 *            type of the object the service must return
 */
public abstract class JsonContentService<T extends Serializable> extends AbstractContentService<T> {

	// ============================================================================================
	// ATTRIBUTES
	// ============================================================================================

	private final ObjectMapper mJsonMapper;
	protected Class<T> mGenericType;

	// ============================================================================================
	// CONSTRUCTOR
	// ============================================================================================

	@SuppressWarnings("unchecked")
	public JsonContentService(String name) {
		super(name);
		this.mGenericType = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
		this.mJsonMapper = new ObjectMapper();
	}

	// ============================================================================================
	// METHODS
	// ============================================================================================

	@Override
	protected final T loadDataFromCache(String cacheFileName) {
		T result = null;
		String resultJson = null;
		resultJson = CacheFileUtils.readStringContentFromFile(this, cacheFileName);

		if (resultJson != null) {
			// finally transform json in object
			if (StringUtils.isNotEmpty(resultJson)) {
				try {
					result = mJsonMapper.readValue(resultJson, mGenericType);
				}
				catch (IOException e) {
					Log.e(getClass().getName(),"Unable to restore cache content in an object of type " + mGenericType);
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

	@Override
	protected final void saveDataToCache(T data, String cacheFileName) {
		String resultJson = null;

		// transform the content in json to store it in the cache
		try {
			resultJson = mJsonMapper.writeValueAsString(data);

			// finally store the json in the cache
			if (StringUtils.isNotEmpty(resultJson)) {
				CacheFileUtils.saveStringToFile(this, resultJson, cacheFileName);
			}
			else {
				Log.e(getClass().getName(),"Unable to save web service result into the cache");
			}
		}
		catch (IOException e) {
			Log.e(getClass().getName(),"Unable to save web service result into the cache");
		}
	}

	@Override
	protected String getCacheKey(Bundle extraBundle) {
		return mGenericType.getSimpleName();
	}
}
