package com.octo.android.robospice.persistence.file;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import roboguice.util.temp.Ln;
import android.app.Application;
import android.util.Base64;

import com.octo.android.robospice.persistence.CacheManager;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.ObjectPersister;
import com.octo.android.robospice.persistence.exception.CacheLoadingException;

/**
 * An {@link ObjectPersister} that saves/loads data in a file.
 * @author sni
 * @param <T>
 *            the class of the data to load/save.
 */
public abstract class InFileObjectPersister<T> extends ObjectPersister<T> {

    private static final String ISO_8859_1 = "ISO-8859-1";

    /* package private */
    static final String CACHE_PREFIX_END = "_";

    private final File mCacheFolder;

    private boolean mSanitizeFileNames;

    public InFileObjectPersister(Application application, File cacheFolder, Class<T> clazz) {
        super(application, clazz);
        mCacheFolder = cacheFolder;
        setSanitizedFileNameEnabled(true);
        initCacheFolder();
    }

    public InFileObjectPersister(Application application, Class<T> clazz) {
        super(application, clazz);
        mCacheFolder = new File(application.getCacheDir(), CacheManager.DEFAULT_CACHE_FOLDER);
        setSanitizedFileNameEnabled(true);
        initCacheFolder();
    }

    @Override
    public long getCreationDateInCache(Object cacheKey) throws CacheLoadingException {
        try {
            return getCacheFile(cacheKey).lastModified();
        } catch (Exception e) {
            throw new CacheLoadingException("Data could not be found in cache for cacheKey=" + cacheKey);
        }
    }

    @Override
    public List<Object> getAllCacheKeys() {
        final String prefix = getCachePrefix();
        String[] cacheFileNameList = getCacheFolder().list(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String filename) {
                // patch from florianmski
                return filename.startsWith(prefix);
            }
        });
        List<Object> result = new ArrayList<Object>(cacheFileNameList.length);
        for (String cacheFileName : cacheFileNameList) {
            final String fileName = cacheFileName.substring(prefix.length());
            String sanitizedName = null;
            if (mSanitizeFileNames) {
                try {
                    sanitizedName = new String(Base64.decode(fileName,
                            Base64.URL_SAFE | Base64.NO_WRAP), ISO_8859_1);
                } catch (UnsupportedEncodingException e) {
                    sanitizedName = fileName;
                }
            } else {
                sanitizedName = fileName;
            }
            result.add(sanitizedName);
        }
        return result;
    }

    @Override
    public List<T> loadAllDataFromCache() throws CacheLoadingException {
        List<Object> allCacheKeys = getAllCacheKeys();
        List<T> result = new ArrayList<T>(allCacheKeys.size());
        for (Object key : allCacheKeys) {
            result.add(loadDataFromCache(key, DurationInMillis.ALWAYS_RETURNED));
        }
        return result;
    }

    @Override
    public boolean removeDataFromCache(Object cacheKey) {
        return getCacheFile(cacheKey).delete();
    }

    @Override
    public void removeAllDataFromCache() {
        File cacheFolder = getCacheFolder();
        File[] cacheFileList = cacheFolder.listFiles(new FileFilter() {

            @Override
            public boolean accept(File file) {
                return file.getName().startsWith(getCachePrefix());
            }
        });

        boolean allDeleted = true;
        for (File cacheFile : cacheFileList) {
            allDeleted = cacheFile.delete() && allDeleted;
        }
        if (!allDeleted) {
            Ln.d("Some file could not be deleted from cache.");
        }
    }

    @Override
    public T loadDataFromCache(Object cacheKey, long maxTimeInCache) throws CacheLoadingException {

        File file = getCacheFile(cacheKey);
        if (isCachedAndNotExpired(file, maxTimeInCache)) {
            return readCacheDataFromFile(file);
        }

        return null;
    }

    protected abstract T readCacheDataFromFile(File file) throws CacheLoadingException;

    protected String getCachePrefix() {
        return getClass().getSimpleName() + CACHE_PREFIX_END;
    }

    /**
     * Enables/Disables sanitizing of file names for cached keys.
     *
     * @param sanitize true to enable sanitizing, false otherwise.
     */
    public final void setSanitizedFileNameEnabled(final boolean sanitize) {
        mSanitizeFileNames = sanitize;
    }

    public File getCacheFile(final Object cacheKey) {
        String encoded = null;

        if (mSanitizeFileNames) {
            try {
                encoded = Base64.encodeToString(
                        cacheKey.toString().getBytes(ISO_8859_1),
                        Base64.URL_SAFE | Base64.NO_WRAP);
            } catch (UnsupportedEncodingException e) {
                encoded = cacheKey.toString();
            }
        } else {
            encoded = cacheKey.toString();
        }

        final StringBuffer buffer = new StringBuffer();
        buffer.append(getCachePrefix());
        buffer.append(encoded);
        return new File(getCacheFolder(), buffer.toString());
    }

    private File getCacheFolder() {
        return mCacheFolder;
    }

    protected boolean isCachedAndNotExpired(Object cacheKey, long maxTimeInCacheBeforeExpiry) {
        File cacheFile = getCacheFile(cacheKey);
        return isCachedAndNotExpired(cacheFile, maxTimeInCacheBeforeExpiry);
    }

    protected boolean isCachedAndNotExpired(File cacheFile, long maxTimeInCacheBeforeExpiry) {
        if (cacheFile.exists()) {
            long timeInCache = System.currentTimeMillis() - cacheFile.lastModified();
            if (maxTimeInCacheBeforeExpiry == DurationInMillis.ALWAYS_RETURNED || timeInCache <= maxTimeInCacheBeforeExpiry) {
                return true;
            }
        }
        return false;
    }

    private void initCacheFolder() {
        if (!mCacheFolder.exists() && !mCacheFolder.mkdir()) {
            Ln.e("Unable to create default cache folder: %s", mCacheFolder);
        }
    }

}
