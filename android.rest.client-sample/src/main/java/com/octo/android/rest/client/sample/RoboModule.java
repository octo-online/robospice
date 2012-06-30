package com.octo.android.rest.client.sample;

import com.google.inject.AbstractModule;
import com.octo.android.rest.client.persistence.DataPersistenceManager;
import com.octo.android.rest.client.persistence.json.JSonPersistenceManageFactory;
import com.octo.android.rest.client.persistence.simple.BinaryPersistenceManager;
import com.octo.android.rest.client.persistence.simple.StringPersistenceManager;

public class RoboModule extends AbstractModule {


	@Override
	protected void configure() {
		registerDataPersistenceManagers();
	}

	private void registerDataPersistenceManagers() {
		DataPersistenceManager dataPersistenceManager = new DataPersistenceManager();
		bind(DataPersistenceManager.class).toInstance( dataPersistenceManager);
		
		StringPersistenceManager stringPersistenceManager = new StringPersistenceManager();
		BinaryPersistenceManager binaryContentManager = new BinaryPersistenceManager();
		JSonPersistenceManageFactory jSonPersistenceManageFactory = new JSonPersistenceManageFactory();
		
		//request application injection
		requestInjection(stringPersistenceManager);
		requestInjection(binaryContentManager);
		requestInjection(jSonPersistenceManageFactory);
		
		dataPersistenceManager.registerDataClassPersistenceManager( stringPersistenceManager );
		dataPersistenceManager.registerDataClassPersistenceManager( binaryContentManager );
		dataPersistenceManager.registerDataClassPersistenceManagerFactory( jSonPersistenceManageFactory );
	}

}
