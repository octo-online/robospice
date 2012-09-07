package com.octo.android.rest.client.persistence.file;

import java.io.File;
import java.io.IOException;

import android.app.Application;
import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.MediumTest;

import com.octo.android.rest.client.exception.CacheLoadingException;
import com.octo.android.rest.client.exception.CacheSavingException;
import com.octo.android.rest.client.sample.TestActivity;

@MediumTest
public class InFileObjectPersisterTest extends ActivityInstrumentationTestCase2< TestActivity > {

    InFileObjectPersister< Object > fileBasedClassCacheManager;

    public InFileObjectPersisterTest() {
        super( "com.octo.android.rest.client", TestActivity.class );
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        fileBasedClassCacheManager = new InFileObjectPersisterUnderTest( getActivity().getApplication() );
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        getActivity().finish();
    }

    public void testGetCachePrefix() {
        String actual = fileBasedClassCacheManager.getCachePrefix();
        assertEquals( InFileObjectPersisterUnderTest.class.getSimpleName() + "_", actual );
    }

    public void testRemoveDataFromCache() throws Exception {
        final String TEST_CACHE_KEY = "TEST_CACHE_KEY";
        fileBasedClassCacheManager.saveDataToCacheAndReturnData( new Object(), TEST_CACHE_KEY );

        File cacheFile = fileBasedClassCacheManager.getCacheFile( TEST_CACHE_KEY );
        assertTrue( cacheFile.exists() );

        fileBasedClassCacheManager.removeDataFromCache( TEST_CACHE_KEY );
        assertFalse( cacheFile.exists() );
    }

    public void testRemoveAllDataFromCache() throws Exception {
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
    private final class InFileObjectPersisterUnderTest extends InFileObjectPersister< Object > {
        private InFileObjectPersisterUnderTest( Application application ) {
            super( application );
        }

        @Override
        public boolean canHandleClass( Class< ? > arg0 ) {
            return false;
        }

        @Override
        public Object loadDataFromCache( Object arg0, long arg1 ) throws CacheLoadingException {
            return null;
        }

        @Override
        public Object saveDataToCacheAndReturnData( Object data, Object cacheKey ) throws CacheSavingException {
            try {
                getCacheFile( cacheKey ).createNewFile();
            } catch ( IOException e ) {
                throw new CacheSavingException( e );
            }
            return data;
        }
    }

}
