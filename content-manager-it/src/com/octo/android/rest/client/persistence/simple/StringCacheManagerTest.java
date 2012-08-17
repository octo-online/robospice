package com.octo.android.rest.client.persistence.simple;

import java.io.File;
import java.nio.charset.Charset;
import java.util.List;

import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.MediumTest;

import com.google.common.io.Files;
import com.octo.android.rest.client.persistence.DurationInMillis;
import com.octo.android.rest.client.sample.TestActivity;

@MediumTest
public class StringCacheManagerTest extends ActivityInstrumentationTestCase2< TestActivity > {

    private static final String TEST_CACHE_KEY = "TEST_CACHE_KEY";

    private StringCacheManager stringCacheManager;

    public StringCacheManagerTest() {
        super( "com.octo.android.Rest.client", TestActivity.class );
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        stringCacheManager = new StringCacheManager( getActivity().getApplication() );
    }

    public void testSaveDataToCacheAndReturnData() throws Exception {
        stringCacheManager.saveDataToCacheAndReturnData( "coucou", TEST_CACHE_KEY );

        File cachedFile = stringCacheManager.getCacheFile( TEST_CACHE_KEY );
        assertTrue( cachedFile.exists() );

        List< String > actual = Files.readLines( cachedFile, Charset.forName( "UTF-8" ) );
        assertEquals( 1, actual.size() );
        assertEquals( "coucou", actual.get( 0 ) );
    }

    public void testLoadDataFromCache_no_expiracy() throws Exception {
        File cachedFile = stringCacheManager.getCacheFile( TEST_CACHE_KEY );
        Files.write( "coucou", cachedFile, Charset.forName( "UTF-8" ) );

        String actual = stringCacheManager.loadDataFromCache( TEST_CACHE_KEY, DurationInMillis.ALWAYS );
        assertEquals( "coucou", actual );
    }

    public void testLoadDataFromCache_not_expired() throws Exception {
        File cachedFile = stringCacheManager.getCacheFile( TEST_CACHE_KEY );
        Files.write( "coucou", cachedFile, Charset.forName( "UTF-8" ) );

        String actual = stringCacheManager.loadDataFromCache( TEST_CACHE_KEY, DurationInMillis.ONE_SECOND );
        assertEquals( "coucou", actual );
    }

    public void testLoadDataFromCache_expired() throws Exception {
        File cachedFile = stringCacheManager.getCacheFile( TEST_CACHE_KEY );
        Files.write( "coucou", cachedFile, Charset.forName( "UTF-8" ) );
        cachedFile.setLastModified( System.currentTimeMillis() - 5 * DurationInMillis.ONE_SECOND );

        String actual = stringCacheManager.loadDataFromCache( TEST_CACHE_KEY, DurationInMillis.ONE_SECOND );
        assertNull( actual );
    }

    @Override
    protected void tearDown() throws Exception {
        getActivity().finish();
        stringCacheManager.removeAllDataFromCache();
        super.tearDown();
    }

}
