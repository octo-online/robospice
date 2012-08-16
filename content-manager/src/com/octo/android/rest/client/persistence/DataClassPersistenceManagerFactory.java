package com.octo.android.rest.client.persistence;

public abstract class DataClassPersistenceManagerFactory implements CacheManagerBusElement {

    public abstract < DATA > DataClassPersistenceManager< DATA > createDataPersistenceManager( Class< DATA > clazz );
}
