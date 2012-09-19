package com.octo.android.robospice.persistence;

import com.octo.android.robospice.exception.CacheLoadingException;
import com.octo.android.robospice.exception.CacheSavingException;

/**
 * This interface is mainly used for mocking/testing. Developpers should use directly the class {@link CacheManager} and
 * should not have to implement this interface. Defines the behavior of a cache manager, a bus of
 * {@link ObjectPersister}.
 * 
 * @author sni
 * 
 */
public interface ICacheManager {

    public abstract void addObjectPersisterFactory( ObjectPersisterFactory factory );

    public abstract void removeObjectPersisterFactory( ObjectPersisterFactory factory );

    /**
     * Loads an instance of a class clazz, that is stored in cache under the key cacheKey.
     * 
     * @param clazz
     *            the class of the object that is supposed to be stored in cache.
     * @param cacheKey
     *            the key used to identify this item in cache.
     * @param maxTimeInCacheBeforeExpiry
     *            the maximum time (in ms) an item can be stored in cache before being considered expired.
     * @return an instance of a class clazz, that is stored in cache under the key cacheKey. If the item is not found in
     *         cache or is older than maxTimeInCacheBeforeExpiry, then this method will return null.
     * @throws CacheLoadingException
     *             if an error occurs during cache reading, or instance creation.
     */
    public abstract < T > T loadDataFromCache( Class< T > clazz, Object cacheKey, long maxTimeInCacheBeforeExpiry ) throws CacheLoadingException;

    /**
     * Save an instance of a given class, into the cache identified by cacheKey. Some {@link ObjectPersister} can modify
     * the data they receive before saving it. Most {@link ObjectPersister} instances will just save the data as-is, in
     * this case, they can even return it and save it asynchronously in a background thread for a better efficiency.
     * 
     * @param data
     *            the data to be saved in cache.
     * @param cacheKey
     *            the key used to identify this item in cache.
     * @return the data that was saved.
     * @throws CacheSavingException
     *             if an error occurs during cache writing.
     */
    public abstract < T > T saveDataToCacheAndReturnData( T data, Object cacheKey ) throws CacheSavingException;

    /**
     * Removes a given data in the cache that is an instance of class clazz.
     * 
     * @param clazz
     *            the class of the data to be removed.
     * @param cacheKey
     *            the identifier of the data to be removed from cache.
     * @return a boolean indicating whether or not this data could be removed.
     */
    public abstract boolean removeDataFromCache( Class< ? > clazz, Object cacheKey );

    /**
     * Removes all data in the cache that are instances of class clazz.
     * 
     * @param clazz
     *            the class of the data to be removed.
     */
    public abstract void removeAllDataFromCache( Class< ? > clazz );

    /**
     * Removes all data in the cache.
     */
    public abstract void removeAllDataFromCache();

}