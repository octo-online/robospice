package com.octo.android.rest.client.persistence;

public abstract class ClassCacheManagerFactory implements CacheManagerBusElement {

    public abstract < DATA > ClassCacheManager< DATA > createDataPersistenceManager( Class< DATA > clazz );
}
