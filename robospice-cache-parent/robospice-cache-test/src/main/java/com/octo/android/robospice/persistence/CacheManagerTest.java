package com.octo.android.robospice.persistence;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.Application;
import android.test.InstrumentationTestCase;
import android.test.suitebuilder.annotation.SmallTest;

import com.octo.android.robospice.persistence.exception.CacheLoadingException;
import com.octo.android.robospice.persistence.exception.CacheSavingException;

@SmallTest
public class CacheManagerTest extends InstrumentationTestCase {
    private static final String TEST_PERSISTED_STRING = "TEST";
    private static final Integer TEST_PERSISTED_INTEGER = Integer.valueOf(0);
    private static final String TEST_FILE = "test.txt";

    private CacheManager cacheManager;

    private Application mApplication;

    @Override
    protected void setUp() throws Exception {
        mApplication = (Application) getInstrumentation().getTargetContext().getApplicationContext();
        cacheManager = new CacheManager();
        cacheManager.setApplication(mApplication);
    }

    public void testGetObjectPersister_fails_to_return_a_persister_when_no_persister_is_registered() {
        // given

        try {
            // when
            cacheManager.getObjectPersister(Object.class);
            fail("No data class persistence manager should have been found as none had been registered");
        } catch (Exception ex) {
            // then
            assertTrue(true);
        }
    }

    public void testGetObjectPersister_returns_a_persister_when_one_persister_is_registered() {
        // given
        MockStringPersistenceManager mockStringPersistenceManager = new MockStringPersistenceManager();
        cacheManager.addPersister(mockStringPersistenceManager);

        // when
        ObjectPersister<?> actual = cacheManager.getObjectPersister(String.class);

        // then
        assertEquals(mockStringPersistenceManager, actual);
    }

    public void testGetObjectPersister_returns_persister_in_order_when_two_persisters_are_registered() {
        // given
        // register a data class persistence manager first
        MockStringPersistenceManager mockStringPersistenceManager = new MockStringPersistenceManager();
        cacheManager.addPersister(mockStringPersistenceManager);

        // register a second data class persistence manager
        MockStringPersistenceManager mockDataClassPersistenceManager2 = new MockStringPersistenceManager();
        cacheManager.addPersister(mockDataClassPersistenceManager2);

        // when
        ObjectPersister<?> actual = cacheManager.getObjectPersister(String.class);

        // then
        assertEquals(mockStringPersistenceManager, actual);
    }

    public void testGetObjectPersister_returns_no_persister_when_persister_has_been_unregistered() {
        // given
        // register a data class persistence manager first
        MockStringPersistenceManager mockStringPersistenceManager = new MockStringPersistenceManager();
        cacheManager.addPersister(mockStringPersistenceManager);
        ObjectPersister<?> actual = cacheManager.getObjectPersister(String.class);
        assertEquals(mockStringPersistenceManager, actual);

        // unregister it
        cacheManager.removePersister(mockStringPersistenceManager);

        // no persistence manager should be found any more
        try {
            // when
            cacheManager.getObjectPersister(String.class);
            fail("No data class persistence manager should have been found as none had been registered");
        } catch (Exception ex) {
            // then
            assertTrue(true);
        }
    }

    public void testGetObjectPersister_returns_no_persister_when_two_were_added_and_goog_persister_has_been_unregistered() {
        // given
        // register 2 data class persistence manager first
        MockStringPersistenceManager mockStringPersistenceManager = new MockStringPersistenceManager();
        cacheManager.addPersister(mockStringPersistenceManager);
        MockIntegerPersistenceManager mockIntegerPersistenceManager = new MockIntegerPersistenceManager();
        cacheManager.addPersister(mockIntegerPersistenceManager);

        // when
        ObjectPersister<?> persisterString = cacheManager.getObjectPersister(String.class);
        ObjectPersister<?> persisterInteger = cacheManager.getObjectPersister(Integer.class);

        // then
        assertEquals(mockStringPersistenceManager, persisterString);
        assertEquals(mockIntegerPersistenceManager, persisterInteger);

        // given
        // unregister it
        cacheManager.removePersister(mockStringPersistenceManager);

        // no persistence manager should be found any more
        try {
            // when
            cacheManager.getObjectPersister(String.class);
            fail("No data class persistence manager should have been found as none had been registered");
        } catch (Exception ex) {
            // then
            assertTrue(true);
        }

        // when
        persisterInteger = cacheManager.getObjectPersister(Integer.class);

        // then
        assertEquals(mockIntegerPersistenceManager, persisterInteger);
    }

    public void testRemoveAllDataFromCache_removes_default_cache_folder() throws IOException {
        // given

        final File defaultCacheFolder = new File(mApplication.getCacheDir(), CacheManager.DEFAULT_CACHE_FOLDER);
        assertTrue(defaultCacheFolder.exists() || defaultCacheFolder.mkdir());

        final File anyFile = new File(defaultCacheFolder, TEST_FILE);
        assertTrue(anyFile.exists() || anyFile.createNewFile());

        // when
        cacheManager.removeAllDataFromCache();

        // then
        assertTrue(!defaultCacheFolder.exists());
    }

    private class MockStringPersistenceManager extends ObjectPersister<String> {

        public MockStringPersistenceManager() {
            super(null, String.class);
        }

        @Override
        public boolean canHandleClass(Class<?> arg0) {
            return arg0.equals(String.class);
        }

        @Override
        public String loadDataFromCache(Object arg0, long arg1) throws CacheLoadingException {
            return TEST_PERSISTED_STRING;
        }

        @Override
        public String saveDataToCacheAndReturnData(String arg0, Object arg1) throws CacheSavingException {
            return TEST_PERSISTED_STRING;
        }

        @Override
        public void removeAllDataFromCache() {
        }

        @Override
        public boolean removeDataFromCache(Object arg0) {
            return true;
        }

        @Override
        public List<String> loadAllDataFromCache() throws CacheLoadingException {
            ArrayList<String> listString = new ArrayList<String>();
            listString.add(TEST_PERSISTED_STRING);
            return listString;
        }

        @Override
        public List<Object> getAllCacheKeys() {
            return null;
        }

        @Override
        public long getCreationDateInCache(Object cacheKey) throws CacheLoadingException {
            return 0;
        }
    }

    private class MockIntegerPersistenceManager extends ObjectPersister<Integer> {

        public MockIntegerPersistenceManager() {
            super(null, Integer.class);
        }

        @Override
        public boolean canHandleClass(Class<?> arg0) {
            return arg0.equals(Integer.class);
        }

        @Override
        public Integer loadDataFromCache(Object arg0, long arg1) throws CacheLoadingException {
            return TEST_PERSISTED_INTEGER;
        }

        @Override
        public Integer saveDataToCacheAndReturnData(Integer arg0, Object arg1) throws CacheSavingException {
            return TEST_PERSISTED_INTEGER;
        }

        @Override
        public void removeAllDataFromCache() {
        }

        @Override
        public boolean removeDataFromCache(Object arg0) {
            return true;
        }

        @Override
        public List<Integer> loadAllDataFromCache() throws CacheLoadingException {
            ArrayList<Integer> listString = new ArrayList<Integer>();
            listString.add(TEST_PERSISTED_INTEGER);
            return listString;
        }

        @Override
        public List<Object> getAllCacheKeys() {
            return null;
        }

        @Override
        public long getCreationDateInCache(Object cacheKey) throws CacheLoadingException {
            return 0;
        }
    }
}
