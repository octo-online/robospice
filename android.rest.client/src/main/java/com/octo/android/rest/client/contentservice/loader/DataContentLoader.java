package com.octo.android.rest.client.contentservice.loader;

import java.io.FileNotFoundException;
import java.io.IOException;

import android.app.Application;

import com.google.inject.Inject;

public abstract class DataContentLoader<DATA> {

	private @Inject Application mApplication;
	
	protected final Application getApplication() {
		return mApplication;
	}

	public abstract boolean canHandleData( Class<?> clazz );
	
	public abstract DATA loadDataFromCache(Class<DATA> clazz, String cacheFileName) throws FileNotFoundException, IOException;
	public abstract DATA saveDataToCacheAndReturnData(DATA data, String cacheFileName) throws FileNotFoundException, IOException;

}
