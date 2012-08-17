package com.octo.android.rest.client.persistence;

import java.io.FileNotFoundException;
import java.io.IOException;

import android.app.Application;

import com.octo.android.rest.client.exception.CacheLoadingException;
import com.octo.android.rest.client.exception.CacheSavingException;

/**
 * Super class of all entities responsible for loading/saving objets of a given class in the cache.
 * 
 * @author sni
 * 
 * @param <DATA>
 *            the class of the objects this {@link ClassCacheManager} can persist/unpersist.
 */
public abstract class ClassCacheManager<DATA> extends ClassCacheManagerFactory {

	public ClassCacheManager(Application application) {
		super(application);
	}

	@Override
	@SuppressWarnings("unchecked")
	public final <T> ClassCacheManager<T> createClassCacheManager(Class<T> clazz) {
		return (ClassCacheManager<T>) this;
	}

	/**
	 * Load data from cache if not expired.
	 * 
	 * @param cacheKey
	 *            the cacheKey of the data to load.
	 * @param maxTimeInCache
	 *            the maximum time the data can have been stored in cached before being considered expired. 0 means infinite.
	 * @return the data if it could be loaded.
	 * @throws FileNotFoundException
	 *             if the data was not in cache.
	 * @throws IOException
	 *             if the data in cache can't be read.
	 * @throws CacheExpiredException
	 *             if the data in cache is expired.
	 */
	public abstract DATA loadDataFromCache(Object cacheKey, long maxTimeInCache) throws CacheLoadingException;

	public abstract DATA saveDataToCacheAndReturnData(DATA data, Object cacheKey) throws CacheSavingException;

	public abstract boolean removeDataFromCache(Object cacheKey);

}
