package com.octo.android.rest.client.persistence;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

/**
 * An entity responsible for loading/saving data from/to cache. It implements a Chain of Responsability pattern,
 * delegating loading and saving operations to {@link ClassCacheManager} and {@link ClassCacheManagerFactory} elements.
 * 
 * The chain of responsibility is ordered. This means that the order used to register elements matters. All elements in
 * the chain of responsibility are questioned in order. The first element that can handle a given class for persistence
 * will be used to persist data of this class.
 * 
 * @author sni
 * 
 */
public class CacheManager {

    private Collection< CacheManagerBusElement > cacheManagerBusElementList = new ArrayList< CacheManagerBusElement >();

    public void registerCacheManagerBusElement( CacheManagerBusElement cacheManagerBusElement ) {
        cacheManagerBusElementList.add( cacheManagerBusElement );
    }

    public void unregisterCacheManagerBusElement( CacheManagerBusElement cacheManagerBusElement ) {
        cacheManagerBusElementList.remove( cacheManagerBusElement );
    }

    public < T > T loadDataFromCache( Class< T > clazz, Object cacheKey, long maxTimeInCacheBeforeExpiry ) throws FileNotFoundException, IOException,
            CacheExpiredException {
        return getClassCacheManager( clazz ).loadDataFromCache( cacheKey, maxTimeInCacheBeforeExpiry );
    }

    @SuppressWarnings("unchecked")
    public < T > T saveDataToCacheAndReturnData( T data, Object cacheKey ) throws FileNotFoundException, IOException {
        ClassCacheManager< T > classCacheManager = (ClassCacheManager< T >) getClassCacheManager( data.getClass() );
        return classCacheManager.saveDataToCacheAndReturnData( data, cacheKey );
    }

    public boolean removeDataFromCache( Class< ? > clazz, Object cacheKey ) {
        return getClassCacheManager( clazz ).removeDataFromCache( cacheKey );
    }

    public void removeAllDataFromCache( Class< ? > clazz ) {
        getClassCacheManager( clazz ).removeAllDataFromCache();
    }

    public void removeAllDataFromCache() {
        for ( CacheManagerBusElement cacheManagerBusElement : this.cacheManagerBusElementList ) {
            cacheManagerBusElement.removeAllDataFromCache();
        }
    }

    @SuppressWarnings("unchecked")
    protected < T > ClassCacheManager< T > getClassCacheManager( Class< T > clazz ) {
        for ( CacheManagerBusElement cacheManagerBusElement : this.cacheManagerBusElementList ) {
            if ( cacheManagerBusElement.canHandleClass( clazz ) ) {
                if ( cacheManagerBusElement instanceof ClassCacheManager< ? > ) {
                    return (ClassCacheManager< T >) cacheManagerBusElement;
                } else if ( cacheManagerBusElement instanceof ClassCacheManagerFactory ) {
                    ClassCacheManagerFactory factory = (ClassCacheManagerFactory) cacheManagerBusElement;
                    return factory.createClassCacheManager( clazz );
                }
            }
        }
        throw new IllegalArgumentException( "Class " + clazz.getName() + " is not handled by any registered CacheManagerBusElementList" );
    }
}
