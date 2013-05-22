package com.octo.android.robospice.persistence.file;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashSet;

import android.app.Application;
import android.test.InstrumentationTestCase;
import android.test.suitebuilder.annotation.MediumTest;
import android.util.Base64;

import com.octo.android.robospice.persistence.CacheManager;
import com.octo.android.robospice.persistence.exception.CacheLoadingException;
import com.octo.android.robospice.persistence.exception.CacheSavingException;

@MediumTest
public class InFileObjectPersisterTest extends InstrumentationTestCase {

    InFileObjectPersister<Object> inFileObjectPersister;

    private Application mApplication;

    private static final String TEST_CACHE_KEY = "TEST_CACHE_KEY";
    private static final String TEST_CACHE_KEY2 = "TEST_CACHE_KEY2";

    private static final String OTHER_FOLDER_NAME = "spicerobo";

    private static final String[] NAMES = {"+-/*NAME", "NAME+-/*", "+-NAME/*",
        "++NAME++", "--NAME--", "+NA--ME+"};

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mApplication = (Application) getInstrumentation().getTargetContext().getApplicationContext();
        inFileObjectPersister = new InFileObjectPersisterUnderTest(mApplication);
    }

    @Override
    protected void tearDown() throws Exception {
        inFileObjectPersister.removeAllDataFromCache();
        super.tearDown();
    }

    public void testGetCachePrefix() {
        String actual = inFileObjectPersister.getCachePrefix();
        assertEquals(InFileObjectPersisterUnderTest.class.getSimpleName() + "_", actual);
    }

    public void testRemoveDataFromCache() throws Exception {
        inFileObjectPersister.saveDataToCacheAndReturnData(new Object(), TEST_CACHE_KEY);

        File cacheFile = inFileObjectPersister.getCacheFile(TEST_CACHE_KEY);
        assertTrue(cacheFile.exists());

        inFileObjectPersister.removeDataFromCache(TEST_CACHE_KEY);
        assertFalse(cacheFile.exists());
    }

    public void testRemoveAllDataFromCache() throws Exception {
        inFileObjectPersister.saveDataToCacheAndReturnData(new Object(), TEST_CACHE_KEY);

        inFileObjectPersister.saveDataToCacheAndReturnData(new Object(), TEST_CACHE_KEY2);

        File cacheFile = inFileObjectPersister.getCacheFile(TEST_CACHE_KEY);
        assertTrue(cacheFile.exists());

        File cacheFile2 = inFileObjectPersister.getCacheFile(TEST_CACHE_KEY2);
        assertTrue(cacheFile2.exists());

        inFileObjectPersister.removeAllDataFromCache();
        assertFalse(cacheFile.exists());
        assertFalse(cacheFile2.exists());
    }

    public void testSavedDataWasStoredOnCorrectPath() throws CacheSavingException {
        inFileObjectPersister.saveDataToCacheAndReturnData(new Object(), TEST_CACHE_KEY);

        final File cacheFile = inFileObjectPersister.getCacheFile(TEST_CACHE_KEY);
        final File expectedParent = new File(mApplication.getCacheDir(), CacheManager.DEFAULT_CACHE_FOLDER);
        assertEquals(expectedParent.getAbsolutePath(), cacheFile.getParent());
        assertTrue(cacheFile.exists());
    }

    public void testCustomPathFileObjectPersisterConstructor() throws CacheSavingException {
        final File cacheFolder = new File(mApplication.getCacheDir(), OTHER_FOLDER_NAME);
        final InFileObjectPersisterUnderTest otherFileObjectPersister = new InFileObjectPersisterUnderTest(mApplication, cacheFolder);

        otherFileObjectPersister.saveDataToCacheAndReturnData(new Object(), TEST_CACHE_KEY);

        final File cacheFile = otherFileObjectPersister.getCacheFile(TEST_CACHE_KEY);
        assertEquals(cacheFolder.getAbsolutePath(), cacheFile.getParent());
        assertTrue(cacheFile.exists());
    }

    public void testCacheFileNameSanitizing() throws CacheSavingException {
        final HashSet<String> names = new HashSet<String>();
        for (int i = 0; i < NAMES.length; i++) {
            inFileObjectPersister.saveDataToCacheAndReturnData(new Object(), NAMES[i]);
            final File cacheFile = inFileObjectPersister.getCacheFile(NAMES[i]);
            assertSanitizedName(cacheFile.getName(), NAMES[i], true);
            assertTrue(cacheFile.exists());
            names.add(cacheFile.getName());
        }
        assertEquals(NAMES.length, names.size());
    }

    public void testSanitizingCanBeEnabledAndDisabled() throws CacheSavingException {
        inFileObjectPersister.saveDataToCacheAndReturnData(new Object(), "$$FILE$$");
        File cacheFile = inFileObjectPersister.getCacheFile("$$FILE$$");
        assertTrue(cacheFile.exists());
        assertSanitizedName(cacheFile.getName(), "$$FILE$$", true);

        inFileObjectPersister.setSanitizedFileNameEnabled(false);

        inFileObjectPersister.saveDataToCacheAndReturnData(new Object(), "$$FILE$$");
        File cacheFile2 = inFileObjectPersister.getCacheFile("$$FILE$$");
        assertTrue(cacheFile2.exists());
        assertSanitizedName(cacheFile2.getName(), "$$FILE$$", false);

        inFileObjectPersister.removeAllDataFromCache();
        assertFalse(cacheFile.exists());
        assertFalse(cacheFile2.exists());
    }

    private void assertSanitizedName(final String actual, final String name,
            final boolean assertion) {
        String encoded = null;
        try {
            encoded = Base64.encodeToString(name.toString()
                    .getBytes("ISO-8859-1"), Base64.URL_SAFE | Base64.NO_WRAP);
        } catch (UnsupportedEncodingException e) {
            encoded = name.toString();
        }

        if (assertion) {
            assertTrue(actual.endsWith(encoded));
        } else {
            assertFalse(actual.endsWith(encoded));
        }
    }

    // ============================================================================================
    // CLASS UNDER TEST
    // ============================================================================================
    private final class InFileObjectPersisterUnderTest extends InFileObjectPersister<Object> {
        private InFileObjectPersisterUnderTest(Application application, File cacheFolder) {
            super(application, cacheFolder, Object.class);
        }

        private InFileObjectPersisterUnderTest(Application application) {
            super(application, Object.class);
        }

        @Override
        public boolean canHandleClass(Class<?> arg0) {
            return false;
        }

        @Override
        public Object loadDataFromCache(Object arg0, long arg1) throws CacheLoadingException {
            return null;
        }

        @Override
        protected Object readCacheDataFromFile(File file) throws CacheLoadingException {
            return null;
        }

        @Override
        public Object saveDataToCacheAndReturnData(Object data, Object cacheKey) throws CacheSavingException {
            try {
                getCacheFile(cacheKey).createNewFile();
            } catch (IOException e) {
                throw new CacheSavingException(e);
            }
            return data;
        }
    }

}
