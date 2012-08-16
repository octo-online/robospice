package com.octo.android.rest.client.persistence.json;

import android.app.Application;

import com.octo.android.rest.client.persistence.DataClassPersistenceManager;
import com.octo.android.rest.client.persistence.DataClassPersistenceManagerFactory;

public class JSonPersistenceManageFactory extends DataClassPersistenceManagerFactory {

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
    public < DATA > DataClassPersistenceManager< DATA > createDataPersistenceManager( Class< DATA > clazz ) {
        return new JSonPersistenceManager( application, clazz );
    }

}
