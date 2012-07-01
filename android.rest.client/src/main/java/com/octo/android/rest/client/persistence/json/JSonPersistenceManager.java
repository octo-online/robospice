package com.octo.android.rest.client.persistence.json;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.nio.charset.Charset;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import android.app.Application;
import android.util.Log;

import com.google.common.base.Strings;
import com.google.common.io.CharStreams;
import com.google.common.io.Files;
import com.octo.android.rest.client.persistence.DataClassPersistenceManager;

public final class JSonPersistenceManager<T  extends Serializable> extends DataClassPersistenceManager<T> {

	// ============================================================================================
	// ATTRIBUTES
	// ============================================================================================

	private final ObjectMapper mJsonMapper;

	private Class<T> clazz;

	// ============================================================================================
	// CONSTRUCTOR
	// ============================================================================================
	public JSonPersistenceManager( Application application, Class<T> clazz) {
		super(application);
		this.clazz = clazz;
		this.mJsonMapper = new ObjectMapper();
	}

	// ============================================================================================
	// METHODS
	// ============================================================================================

	public final T loadDataFromCache( String cacheFileName) throws JsonParseException, JsonMappingException, IOException {
		T result = null;
		String resultJson = null;

		resultJson =  CharStreams.toString(Files.newReader(new File(getApplication().getCacheDir(), cacheFileName),Charset.forName("UTF-8") ) );

		if (resultJson != null) {
			// finally transform json in object
			if (!Strings.isNullOrEmpty(resultJson)) {
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
		if (!Strings.isNullOrEmpty(resultJson)) {
			Files.write(resultJson, new File(getApplication().getCacheDir(), cacheFileName), Charset.forName("UTF-8"));
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
