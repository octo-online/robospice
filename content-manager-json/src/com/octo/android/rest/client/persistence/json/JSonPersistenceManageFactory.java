package com.octo.android.rest.client.persistence.json;

import android.app.Application;

import com.octo.android.rest.client.persistence.file.InFileObjectPersister;
import com.octo.android.rest.client.persistence.file.InFileObjectPersisterFactory;

public class JSonPersistenceManageFactory extends InFileObjectPersisterFactory {

    public JSonPersistenceManageFactory( Application application ) {
        super( application );
    }

    @Override
    public boolean canHandleClass( Class< ? > clazz ) {
        return true;
    }

    @Override
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public < DATA > InFileObjectPersister< DATA > createClassCacheManager( Class< DATA > clazz ) {
        return new JSonPersistenceManager( getApplication(), clazz, getCachePrefix() );
    }

}
