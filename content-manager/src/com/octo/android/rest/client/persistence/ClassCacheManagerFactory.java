package com.octo.android.rest.client.persistence;

import android.app.Application;

public abstract class ClassCacheManagerFactory implements CacheManagerBusElement {

	private Application mApplication;

	public ClassCacheManagerFactory(Application application) {
		this.mApplication = application;
	}

	protected final Application getApplication() {
		return mApplication;
	}

	public abstract <DATA> ClassCacheManager<DATA> createDataPersistenceManager(Class<DATA> clazz);
}
