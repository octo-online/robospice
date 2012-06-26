package com.octo.android.rest.client.contentservice;

import java.io.Serializable;

import android.app.Application;

import com.google.inject.Inject;
import com.octo.android.rest.client.contentservice.persistence.DataClassPersistenceManager;
import com.octo.android.rest.client.contentservice.persistence.DataClassPersistenceManagerFactory;
import com.octo.android.rest.client.contentservice.persistence.JSonPersistenceManager;

public class JSonPersistenceManageFactory extends DataClassPersistenceManagerFactory {

	Application application;
	
	@Inject
	public JSonPersistenceManageFactory(Application application) {
		this.application = application;
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

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public <DATA> DataClassPersistenceManager<DATA> createDataPersistenceManager(
			Class<DATA> clazz) {
		return (DataClassPersistenceManager<DATA>)new JSonPersistenceManager( application );
	}

}
