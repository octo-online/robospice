package com.octo.android.rest.client.persistence.simple;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import android.test.ActivityInstrumentationTestCase2;

import com.google.common.io.ByteStreams;
import com.google.common.io.Files;
import com.octo.android.rest.client.persistence.CacheExpiredException;
import com.octo.android.rest.client.sample.HelloAndroidActivity;

public class InputStreamCacheManagerTest extends ActivityInstrumentationTestCase2< HelloAndroidActivity > {

    private static final String TEST_CACHE_KEY = "TEST_CACHE_KEY";

    private InputStreamCacheManager inputStreamCacheManager;

    public InputStreamCacheManagerTest() {
        super( "com.octo.android.Rest.client", HelloAndroidActivity.class );
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        inputStreamCacheManager = new InputStreamCacheManager( getActivity().getApplication() );
    }

    public void testSaveDataToCacheAndReturnData() throws FileNotFoundException, IOException {
        inputStreamCacheManager.saveDataToCacheAndReturnData( new ByteArrayInputStream( "coucou".getBytes() ), TEST_CACHE_KEY );

        File cachedFile = inputStreamCacheManager.getCacheFile( TEST_CACHE_KEY );
        assertTrue( cachedFile.exists() );

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ByteStreams.copy( new FileInputStream( cachedFile ), bos );
        assertEquals( "coucou".getBytes(), bos.toByteArray() );
    }

    public void testLoadDataFromCache() throws FileNotFoundException, IOException, CacheExpiredException {
        File cachedFile = inputStreamCacheManager.getCacheFile( TEST_CACHE_KEY );
        Files.write( "coucou".getBytes(), cachedFile );

        byte[] actual = Files.toByteArray( cachedFile );
        assertEquals( "coucou".getBytes(), actual );
    }

    @Override
    protected void tearDown() throws Exception {
        getActivity().finish();
        inputStreamCacheManager.removeAllDataFromCache();
        super.tearDown();
    }

}
