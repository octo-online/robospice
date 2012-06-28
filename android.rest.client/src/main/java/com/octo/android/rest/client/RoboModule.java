package com.octo.android.rest.client;

import com.google.inject.AbstractModule;
import com.octo.android.rest.client.contentservice.persistence.BinaryPersistenceManager;
import com.octo.android.rest.client.contentservice.persistence.DataPersistenceManager;
import com.octo.android.rest.client.contentservice.persistence.JSonPersistenceManageFactory;
import com.octo.android.rest.client.contentservice.persistence.StringPersistenceManager;

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
