package com.octo.android.rest.client.persistence;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.SmallTest;

import com.google.common.io.ByteStreams;
import com.octo.android.rest.client.persistence.simple.InputStreamCacheManager;
import com.octo.android.rest.client.sample.TestActivity;

@SmallTest
public class InputStreamCacheManagerTest extends ActivityInstrumentationTestCase2< TestActivity > {

    private InputStreamCacheManager binaryPersistenceManager;

    public InputStreamCacheManagerTest() {
        super( "com.octo.android.rest.client.sample", TestActivity.class );
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        binaryPersistenceManager = new InputStreamCacheManager( getActivity().getApplication() );
    }

    public void test_canHandleInputStreams() {
        boolean canHandleStrings = binaryPersistenceManager.canHandleClass( InputStream.class );
        assertEquals( true, canHandleStrings );
    }

    public void test_saveDataAndReturnData() throws Exception {
        byte[] bytes = "coucou".getBytes();
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream( bytes );
        final String FILE_NAME = "toto";
        InputStream inputStreamReturned = binaryPersistenceManager.saveDataToCacheAndReturnData( byteArrayInputStream, FILE_NAME );
        bytes = ByteStreams.toByteArray( inputStreamReturned );
        assertEquals( "coucou", new String( bytes ) );
    }

    public void test_loadDataFromCache() throws Exception {
        final String FILE_NAME = "toto";
        byte[] bytes = "coucou".getBytes();
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream( bytes );
        binaryPersistenceManager.saveDataToCacheAndReturnData( byteArrayInputStream, FILE_NAME );
        InputStream inputStreamReturned = binaryPersistenceManager.loadDataFromCache( FILE_NAME, 0 );
        bytes = ByteStreams.toByteArray( inputStreamReturned );
        assertEquals( "coucou", new String( bytes ) );
    }

    @Override
    protected void tearDown() throws Exception {
        getActivity().finish();
        super.tearDown();
    }
}
