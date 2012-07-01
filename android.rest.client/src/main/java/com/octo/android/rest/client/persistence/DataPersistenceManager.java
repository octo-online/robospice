package com.octo.android.rest.client.persistence;

import java.util.Collection;
import java.util.HashSet;

public class DataPersistenceManager {

	private Collection<DataClassPersistenceManager<?>> dataPersistenceManagerList = new HashSet<DataClassPersistenceManager<?>>();
	private Collection<DataClassPersistenceManagerFactory> dataPersistenceManagerFactoryList = new HashSet<DataClassPersistenceManagerFactory>();

	public void registerDataClassPersistenceManager(DataClassPersistenceManager<?> dataClassPersistenceManager) {
		dataPersistenceManagerList.add(dataClassPersistenceManager);
	}
	
	public void unregisterDataClassPersistenceManager(DataClassPersistenceManager<?> dataClassPersistenceManager) {
		dataPersistenceManagerList.remove(dataClassPersistenceManager);
	}
	
	public void registerDataClassPersistenceManagerFactory(DataClassPersistenceManagerFactory dataClassPersistenceManagerFactory) {
		dataPersistenceManagerFactoryList.add(dataClassPersistenceManagerFactory);
	}
	
	public void unregisterDataClassPersistenceManagerFactory(DataClassPersistenceManagerFactory dataClassPersistenceManagerFactory) {
		dataPersistenceManagerFactoryList.remove(dataClassPersistenceManagerFactory);
	}
	
	@SuppressWarnings("unchecked")
	public <T> DataClassPersistenceManager<T> getDataClassPersistenceManager(Class<T> clazz) {
		for( DataClassPersistenceManager<?> dataPersistenceManager : this.dataPersistenceManagerList ) {
			if( dataPersistenceManager.canHandleData(clazz)) {
				return (DataClassPersistenceManager<T>)dataPersistenceManager;
			}
		}
		
		for( DataClassPersistenceManagerFactory dataPersistenceManagerFactory : this.dataPersistenceManagerFactoryList ) {
			if( dataPersistenceManagerFactory.canHandleData(clazz)) {
				return dataPersistenceManagerFactory.createDataPersistenceManager(clazz);
			}
		}
		throw new IllegalArgumentException( "Class "+clazz.getName() + " is not handled by any registered DataClassPersistenceManager or factory" );
	}
}
