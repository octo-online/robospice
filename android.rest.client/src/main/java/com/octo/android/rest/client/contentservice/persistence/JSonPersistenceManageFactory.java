package com.octo.android.rest.client.contentservice.persistence;

import java.io.Serializable;

import android.app.Application;

import com.google.inject.Inject;

public class JSonPersistenceManageFactory extends DataClassPersistenceManagerFactory {

	@Inject
	Application application;
	
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
