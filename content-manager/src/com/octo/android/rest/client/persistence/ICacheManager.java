package com.octo.android.rest.client.persistence;

import com.octo.android.rest.client.exception.CacheLoadingException;
import com.octo.android.rest.client.exception.CacheSavingException;

public interface ICacheManager {

	public abstract void registerFactory(ClassCacheManagerFactory factory);

	public abstract void unregisterFactory(ClassCacheManagerFactory factory);

	public abstract <T> T loadDataFromCache(Class<T> clazz, Object cacheKey, long maxTimeInCacheBeforeExpiry) throws CacheLoadingException;

	public abstract <T> T saveDataToCacheAndReturnData(T data, Object cacheKey) throws CacheSavingException;

	public abstract boolean removeDataFromCache(Class<?> clazz, Object cacheKey);

	public abstract void removeAllDataFromCache(Class<?> clazz);

	public abstract void removeAllDataFromCache();

}