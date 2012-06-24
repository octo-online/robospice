package com.octo.android.rest.client.contentservice.loader;

import android.app.Application;

import com.google.inject.Inject;

public abstract class DataContentLoader<DATA> {

	private @Inject Application mApplication;
	
	protected final Application getApplication() {
		return mApplication;
	}

	public abstract boolean canHandleData( Class<?> clazz );
	
	public abstract DATA loadDataFromCache(String cacheFileName);
	public abstract void saveDataToCache(DATA data, String cacheFileName);

}
