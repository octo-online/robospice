package com.octo.android.rest.client.sample;

import android.app.Application;
import android.content.Context;
import android.rest.client.persistence.json.JSonPersistenceManageFactory;

import com.google.inject.AbstractModule;
import com.octo.android.rest.client.persistence.DataPersistenceManager;
import com.octo.android.rest.client.persistence.simple.BinaryPersistenceManager;
import com.octo.android.rest.client.persistence.simple.StringPersistenceManager;

public class SampleRoboModule extends AbstractModule {

	private Application application;

	public SampleRoboModule(Context context) {
		super();
		this.application = (Application)context;
	}

	@Override
	protected void configure() {
		registerDataPersistenceManagers();
	}

	private void registerDataPersistenceManagers() {
		DataPersistenceManager dataPersistenceManager = new DataPersistenceManager();
		bind(DataPersistenceManager.class).toInstance( dataPersistenceManager);
		
		//init 
		StringPersistenceManager stringPersistenceManager = new StringPersistenceManager(application);
		BinaryPersistenceManager binaryPersistenceManager = new BinaryPersistenceManager(application);
		JSonPersistenceManageFactory jSonPersistenceManageFactory = new JSonPersistenceManageFactory(application);

		//request application injection
		dataPersistenceManager.registerDataClassPersistenceManager(stringPersistenceManager);
		dataPersistenceManager.registerDataClassPersistenceManager(binaryPersistenceManager);
		dataPersistenceManager.registerDataClassPersistenceManagerFactory(jSonPersistenceManageFactory);	}

}
