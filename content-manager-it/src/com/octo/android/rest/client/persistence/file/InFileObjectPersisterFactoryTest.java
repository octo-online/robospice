package com.octo.android.rest.client.persistence.file;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import android.app.Application;
import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.MediumTest;

import com.google.common.io.Files;
import com.octo.android.rest.client.persistence.file.InFileObjectPersister;
import com.octo.android.rest.client.persistence.file.InFileObjectPersisterFactory;
import com.octo.android.rest.client.sample.TestActivity;

@MediumTest
public class InFileObjectPersisterFactoryTest extends ActivityInstrumentationTestCase2< TestActivity > {

    InFileObjectPersisterFactory fileBasedClassCacheManagerFactory;

    public InFileObjectPersisterFactoryTest() {
        super( "com.octo.android.rest.client", TestActivity.class );
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        fileBasedClassCacheManagerFactory = new FileBaseClassCacheManagerFactoryUnderTest( getActivity().getApplication() );
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        getActivity().finish();
    }

    public void testGetCachePrefix() {
        String actual = fileBasedClassCacheManagerFactory.getCachePrefix();
        assertEquals( FileBaseClassCacheManagerFactoryUnderTest.class.getSimpleName() + "_", actual );
    }

    public void testRemoveAllDataFromCache() throws FileNotFoundException, IOException {
        final String TEST_CACHE_KEY = "TEST_CACHE_KEY";

        File cacheDir = getActivity().getApplication().getCacheDir();
        File testFile1 = new File( cacheDir, fileBasedClassCacheManagerFactory.getCachePrefix() + TEST_CACHE_KEY );

        final String TEST_CACHE_KEY2 = "TEST_CACHE_KEY2";
        File testFile2 = new File( cacheDir, fileBasedClassCacheManagerFactory.getCachePrefix() + TEST_CACHE_KEY2 );

        Files.touch( testFile1 );
        Files.touch( testFile2 );

        assertTrue( testFile1.exists() );
        assertTrue( testFile2.exists() );

        fileBasedClassCacheManagerFactory.removeAllDataFromCache();

        assertFalse( testFile1.exists() );
        assertFalse( testFile2.exists() );
    }

    // ============================================================================================
    // CLASS UNDER TEST
    // ============================================================================================
    private final class FileBaseClassCacheManagerFactoryUnderTest extends InFileObjectPersisterFactory {
        private FileBaseClassCacheManagerFactoryUnderTest( Application application ) {
            super( application );
        }

        @Override
        public boolean canHandleClass( Class< ? > arg0 ) {
            return false;
        }

        @Override
        public < DATA > InFileObjectPersister< DATA > createClassCacheManager( Class< DATA > clazz ) {
            return null;
        }

    }

}
