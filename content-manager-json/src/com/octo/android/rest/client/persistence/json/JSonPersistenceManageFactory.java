package com.octo.android.rest.client.persistence.json;

import android.app.Application;

import com.octo.android.rest.client.persistence.ClassCacheManager;
import com.octo.android.rest.client.persistence.ClassCacheManagerFactory;

public class JSonPersistenceManageFactory extends ClassCacheManagerFactory {

    Application application;

    public JSonPersistenceManageFactory( Application application ) {
        this.application = application;
    }

    @Override
    public boolean canHandleClass( Class< ? > clazz ) {
        return true;
    }

    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public < DATA > ClassCacheManager< DATA > createDataPersistenceManager( Class< DATA > clazz ) {
        return new JSonPersistenceManager( application, clazz );
    }

}
