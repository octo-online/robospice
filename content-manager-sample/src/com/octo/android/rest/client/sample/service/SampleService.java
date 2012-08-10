package com.octo.android.rest.client.sample.service;

import android.app.Application;

import com.octo.android.rest.client.ContentService;
import com.octo.android.rest.client.persistence.DataPersistenceManager;
import com.octo.android.rest.client.persistence.json.JSonPersistenceManageFactory;
import com.octo.android.rest.client.persistence.simple.BinaryPersistenceManager;
import com.octo.android.rest.client.persistence.simple.StringPersistenceManager;

public class SampleService extends ContentService {

    @Override
    public DataPersistenceManager createDataPersistenceManager( Application application ) {
        DataPersistenceManager dataPersistenceManager = new DataPersistenceManager();

        // init
        StringPersistenceManager stringPersistenceManager = new StringPersistenceManager( application );
        BinaryPersistenceManager binaryPersistenceManager = new BinaryPersistenceManager( application );
        JSonPersistenceManageFactory jSonPersistenceManageFactory = new JSonPersistenceManageFactory( application );

        // request application injection
        dataPersistenceManager.registerDataClassPersistenceManager( stringPersistenceManager );
        dataPersistenceManager.registerDataClassPersistenceManager( binaryPersistenceManager );
        dataPersistenceManager.registerDataClassPersistenceManagerFactory( jSonPersistenceManageFactory );
        return dataPersistenceManager;
    }

}
