package com.octo.android.rest.client.persistence;


public abstract class DataClassPersistenceManagerFactory {

	public abstract boolean canHandleData( Class<?> clazz );
	public abstract <DATA> DataClassPersistenceManager<DATA> createDataPersistenceManager(Class<DATA> clazz);

}
