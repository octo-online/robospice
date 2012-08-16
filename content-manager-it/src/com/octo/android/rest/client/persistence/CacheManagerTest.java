package com.octo.android.rest.client.persistence;

import java.io.FileNotFoundException;
import java.io.IOException;

import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.SmallTest;

@SmallTest
public class CacheManagerTest extends AndroidTestCase {

    private CacheManager cacheManager;

    @Override
    protected void setUp() throws Exception {
        cacheManager = new CacheManager();
    }

    public void testEmptyDataPersistenceManager() {
        try {
            cacheManager.getClassCacheManager( Object.class );
            fail( "No data class persistence manager should have been found as none had been registered" );
        } catch ( Exception ex ) {
            assertTrue( true );
        }
    }

    public void testRegisterDataClassPersistenceManager() {
        MockDataClassPersistenceManager mockDataClassPersistenceManager = new MockDataClassPersistenceManager();
        cacheManager.registerFactory( mockDataClassPersistenceManager );
        ClassCacheManager< ? > actual = cacheManager.getClassCacheManager( String.class );
        assertEquals( mockDataClassPersistenceManager, actual );
    }

    public void testGetDataClassPersistenceManager_returns_CacheManagerBusElement_in_order() {
        // register a data class persistence manager first
        MockDataClassPersistenceManager mockDataClassPersistenceManager = new MockDataClassPersistenceManager();
        cacheManager.registerFactory( mockDataClassPersistenceManager );

        // register a second data class persistence manager
        MockDataClassPersistenceManager mockDataClassPersistenceManager2 = new MockDataClassPersistenceManager();
        cacheManager.registerFactory( mockDataClassPersistenceManager2 );

        ClassCacheManager< ? > actual = cacheManager.getClassCacheManager( String.class );
        assertEquals( mockDataClassPersistenceManager, actual );
    }

    public void testUnRegisterDataClassPersistenceManager() {
        // register a data class persistence manager first
        MockDataClassPersistenceManager mockDataClassPersistenceManager = new MockDataClassPersistenceManager();
        cacheManager.registerFactory( mockDataClassPersistenceManager );
        ClassCacheManager< ? > actual = cacheManager.getClassCacheManager( String.class );
        assertEquals( mockDataClassPersistenceManager, actual );

        // unregister it
        cacheManager.unregisterFactory( mockDataClassPersistenceManager );

        // no persistence manager should be found any more
        try {
            cacheManager.getClassCacheManager( String.class );
            fail( "No data class persistence manager should have been found as none had been registered" );
        } catch ( Exception ex ) {
            assertTrue( true );
        }
    }

    private class MockDataClassPersistenceManager extends ClassCacheManager< String > {
        private static final String TEST_PERSISTED_STRING = "TEST";

        public MockDataClassPersistenceManager() {
            super( null );
        }

        @Override
        public boolean canHandleClass( Class< ? > arg0 ) {
            return arg0.equals( String.class );
        }

        @Override
        public String loadDataFromCache( Object arg0, long arg1 ) throws FileNotFoundException, IOException, CacheExpiredException {
            return TEST_PERSISTED_STRING;
        }

        @Override
        public String saveDataToCacheAndReturnData( String arg0, Object arg1 ) throws FileNotFoundException, IOException {
            return TEST_PERSISTED_STRING;
        }

        @Override
        public void removeAllDataFromCache() {
        }

        @Override
        public boolean removeDataFromCache( Object arg0 ) {
            return true;
        }
    }
}
