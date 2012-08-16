package com.octo.android.rest.client.persistence.json;

import android.app.Application;

import com.octo.android.rest.client.persistence.simple.FileBasedClassCacheManager;
import com.octo.android.rest.client.persistence.simple.FileBasedClassCacheManagerFactory;

public class JSonPersistenceManageFactory extends FileBasedClassCacheManagerFactory {

    public JSonPersistenceManageFactory( Application application ) {
        super( application );
    }

    @Override
    public boolean canHandleClass( Class< ? > clazz ) {
        return true;
    }

    @Override
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public < DATA > FileBasedClassCacheManager< DATA > createClassCacheManager( Class< DATA > clazz ) {
        return new JSonPersistenceManager( getApplication(), clazz, getCachePrefix() );
    }

}
