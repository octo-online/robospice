package com.octo.android.rest.client.contentservice.persistence;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.ParameterizedType;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import android.app.Application;
import android.util.Log;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public final class JSonPersistenceManager<T  extends Serializable> extends DataClassPersistenceManager<T> {

	Application mApplication;

	// ============================================================================================
	// ATTRIBUTES
	// ============================================================================================

	private final ObjectMapper mJsonMapper;

	// ============================================================================================
	// CONSTRUCTOR
	// ============================================================================================
	@Inject
	public JSonPersistenceManager( Application application) {
		this.mApplication = application;
		this.mJsonMapper = new ObjectMapper();
	}

	// ============================================================================================
	// METHODS
	// ============================================================================================

	@SuppressWarnings("unchecked")
	public final T loadDataFromCache( String cacheFileName) throws JsonParseException, JsonMappingException, IOException {
		T result = null;
		String resultJson = null;
		resultJson =  IOUtils.toString( new FileInputStream( new File(mApplication.getCacheDir(), cacheFileName) ) );

		if (resultJson != null) {
			// finally transform json in object
			if (StringUtils.isNotEmpty(resultJson)) {
				result = mJsonMapper.readValue(resultJson, (Class<T>) ((ParameterizedType) getClass().getSuperclass().getGenericSuperclass()).getActualTypeArguments()[0]);
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
			IOUtils.write(resultJson, new FileOutputStream( new File(mApplication.getCacheDir(), cacheFileName) ) );
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
