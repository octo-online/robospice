package com.octo.android.rest.client.persistence;

import java.io.FileNotFoundException;
import java.io.IOException;

import android.app.Application;

/**
 * Super class of all entities responsible for loading/saving objets of a given class in the cache.
 * 
 * @author sni
 * 
 * @param <DATA>
 *            the class of the objects this {@link ClassCacheManager} can persist/unpersist.
 */
public abstract class ClassCacheManager< DATA > implements CacheManagerBusElement {

    private Application mApplication;

    public ClassCacheManager( Application application ) {
        this.mApplication = application;
    }

    protected final Application getApplication() {
        return mApplication;
    }

    /**
     * Load data from cache if not expired.
     * 
     * @param cacheKey
     *            the cacheKey of the data to load.
     * @param maxTimeInCache
     *            the maximum time the data can have been stored in cached before being considered expired. 0 means
     *            infinite.
     * @return the data if it could be loaded.
     * @throws FileNotFoundException
     *             if the data was not in cache.
     * @throws IOException
     *             if the data in cache can't be read.
     * @throws CacheExpiredException
     *             if the data in cache is expired.
     */
    public abstract DATA loadDataFromCache( Object cacheKey, long maxTimeInCache ) throws FileNotFoundException, IOException, CacheExpiredException;

    public abstract DATA saveDataToCacheAndReturnData( DATA data, Object cacheKey ) throws FileNotFoundException, IOException;

}
