package com.octo.android.rest.client.persistence.json;

import android.app.Application;

import com.octo.android.rest.client.persistence.file.InFileObjectPersister;
import com.octo.android.rest.client.persistence.file.InFileObjectPersisterFactory;

public class InJSonFileObjectPersisterFactory extends InFileObjectPersisterFactory {

    public InJSonFileObjectPersisterFactory( Application application ) {
        super( application );
    }

    @Override
    public boolean canHandleClass( Class< ? > clazz ) {
        return true;
    }

    @Override
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public < DATA > InFileObjectPersister< DATA > createClassCacheManager( Class< DATA > clazz ) {
        InFileObjectPersister inFileObjectPersister = new InJSonFileObjectPersister( getApplication(), clazz, getCachePrefix() );
        inFileObjectPersister.setAsyncSaveEnabled( isAsyncSaveEnabled );
        return inFileObjectPersister;
    }

}
