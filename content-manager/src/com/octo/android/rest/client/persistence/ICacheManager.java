package com.octo.android.rest.client.persistence;

import java.io.FileNotFoundException;
import java.io.IOException;

public interface ICacheManager {

    public abstract void registerFactory( ClassCacheManagerFactory factory );

    public abstract void unregisterFactory( ClassCacheManagerFactory factory );

    public abstract < T > T loadDataFromCache( Class< T > clazz, Object cacheKey, long maxTimeInCacheBeforeExpiry ) throws FileNotFoundException, IOException,
            CacheExpiredException;

    public abstract < T > T saveDataToCacheAndReturnData( T data, Object cacheKey ) throws FileNotFoundException, IOException;

    public abstract boolean removeDataFromCache( Class< ? > clazz, Object cacheKey );

    public abstract void removeAllDataFromCache( Class< ? > clazz );

    public abstract void removeAllDataFromCache();

}