package com.octo.android.rest.client.persistence;

import java.io.FileNotFoundException;
import java.io.IOException;

import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.SmallTest;

@SmallTest
public class DataPersistenceManagerTest extends AndroidTestCase {

    private CacheManager dataPersistenceManager;

    @Override
    protected void setUp() throws Exception {
        dataPersistenceManager = new CacheManager();
    }

    public void testEmptyDataPersistenceManager() {
        try {
            dataPersistenceManager.getDataClassPersistenceManager( Object.class );
            fail( "No data class persistence manager should have been found as none had been registered" );
        } catch ( Exception ex ) {
            assertTrue( true );
        }
    }

    public void testRegisterDataClassPersistenceManager() {
        MockDataClassPersistenceManager mockDataClassPersistenceManager = new MockDataClassPersistenceManager();
        dataPersistenceManager.registerCacheManagerBusElement( mockDataClassPersistenceManager );
        DataClassPersistenceManager< ? > actual = dataPersistenceManager.getDataClassPersistenceManager( String.class );
        assertEquals( mockDataClassPersistenceManager, actual );
    }

    public void testGetDataClassPersistenceManager_returns_CacheManagerBusElement_in_order() {
        // register a data class persistence manager first
        MockDataClassPersistenceManager mockDataClassPersistenceManager = new MockDataClassPersistenceManager();
        dataPersistenceManager.registerCacheManagerBusElement( mockDataClassPersistenceManager );

        // register a second data class persistence manager
        MockDataClassPersistenceManager mockDataClassPersistenceManager2 = new MockDataClassPersistenceManager();
        dataPersistenceManager.registerCacheManagerBusElement( mockDataClassPersistenceManager2 );

        DataClassPersistenceManager< ? > actual = dataPersistenceManager.getDataClassPersistenceManager( String.class );
        assertEquals( mockDataClassPersistenceManager, actual );
    }

    public void testUnRegisterDataClassPersistenceManager() {
        // register a data class persistence manager first
        MockDataClassPersistenceManager mockDataClassPersistenceManager = new MockDataClassPersistenceManager();
        dataPersistenceManager.registerCacheManagerBusElement( mockDataClassPersistenceManager );
        DataClassPersistenceManager< ? > actual = dataPersistenceManager.getDataClassPersistenceManager( String.class );
        assertEquals( mockDataClassPersistenceManager, actual );

        // unregister it
        dataPersistenceManager.unregisterCacheManagerBusElement( mockDataClassPersistenceManager );

        // no persistence manager should be found any more
        try {
            dataPersistenceManager.getDataClassPersistenceManager( String.class );
            fail( "No data class persistence manager should have been found as none had been registered" );
        } catch ( Exception ex ) {
            assertTrue( true );
        }
    }

    private class MockDataClassPersistenceManager extends DataClassPersistenceManager< String > {
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
    }
}
