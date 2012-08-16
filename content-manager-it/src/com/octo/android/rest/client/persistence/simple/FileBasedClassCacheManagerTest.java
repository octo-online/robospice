package com.octo.android.rest.client.persistence.simple;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import android.app.Application;
import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.MediumTest;

import com.octo.android.rest.client.persistence.CacheExpiredException;
import com.octo.android.rest.client.sample.TestActivity;

@MediumTest
public class FileBasedClassCacheManagerTest extends ActivityInstrumentationTestCase2< TestActivity > {

    FileBasedClassCacheManager< Object > fileBasedClassCacheManager;

    public FileBasedClassCacheManagerTest() {
        super( "com.octo.android.rest.client", TestActivity.class );
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        fileBasedClassCacheManager = new FileBaseClassCacheManagerUnderTest( getActivity().getApplication() );
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        getActivity().finish();
    }

    public void testGetCachePrefix() {
        String actual = fileBasedClassCacheManager.getCachePrefix();
        assertEquals( FileBaseClassCacheManagerUnderTest.class.getSimpleName() + "_", actual );
    }

    public void testRemoveDataFromCache() throws FileNotFoundException, IOException {
        final String TEST_CACHE_KEY = "TEST_CACHE_KEY";
        fileBasedClassCacheManager.saveDataToCacheAndReturnData( new Object(), TEST_CACHE_KEY );

        File cacheFile = fileBasedClassCacheManager.getCacheFile( TEST_CACHE_KEY );
        assertTrue( cacheFile.exists() );

        fileBasedClassCacheManager.removeDataFromCache( TEST_CACHE_KEY );
        assertFalse( cacheFile.exists() );
    }

    public void testRemoveAllDataFromCache() throws FileNotFoundException, IOException {
        final String TEST_CACHE_KEY = "TEST_CACHE_KEY";
        fileBasedClassCacheManager.saveDataToCacheAndReturnData( new Object(), TEST_CACHE_KEY );

        final String TEST_CACHE_KEY2 = "TEST_CACHE_KEY2";
        fileBasedClassCacheManager.saveDataToCacheAndReturnData( new Object(), TEST_CACHE_KEY2 );

        File cacheFile = fileBasedClassCacheManager.getCacheFile( TEST_CACHE_KEY );
        assertTrue( cacheFile.exists() );

        File cacheFile2 = fileBasedClassCacheManager.getCacheFile( TEST_CACHE_KEY2 );
        assertTrue( cacheFile2.exists() );

        fileBasedClassCacheManager.removeAllDataFromCache();
        assertFalse( cacheFile.exists() );
        assertFalse( cacheFile2.exists() );
    }

    // ============================================================================================
    // CLASS UNDER TEST
    // ============================================================================================
    private final class FileBaseClassCacheManagerUnderTest extends FileBasedClassCacheManager< Object > {
        private FileBaseClassCacheManagerUnderTest( Application application ) {
            super( application );
        }

        @Override
        public boolean canHandleClass( Class< ? > arg0 ) {
            return false;
        }

        @Override
        public Object loadDataFromCache( Object arg0, long arg1 ) throws FileNotFoundException, IOException, CacheExpiredException {
            return null;
        }

        @Override
        public Object saveDataToCacheAndReturnData( Object data, Object cacheKey ) throws FileNotFoundException, IOException {
            getCacheFile( cacheKey ).createNewFile();
            return data;
        }
    }

}
