package com.octo.android.rest.client.persistence;

import java.io.FileNotFoundException;
import java.io.IOException;

import android.app.Application;

public abstract class DataClassPersistenceManager<DATA> {

	private Application mApplication;
	
	public DataClassPersistenceManager(Application application) {
		this.mApplication = application;
	}
	
	protected final Application getApplication() {
		return mApplication;
	}

	public abstract boolean canHandleData( Class<?> clazz );
	
	public abstract DATA loadDataFromCache(String cacheFileName) throws FileNotFoundException, IOException;
	public abstract DATA saveDataToCacheAndReturnData(DATA data, String cacheFileName) throws FileNotFoundException, IOException;

}
