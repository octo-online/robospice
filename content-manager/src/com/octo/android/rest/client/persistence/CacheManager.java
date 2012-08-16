package com.octo.android.rest.client.persistence;

import java.util.ArrayList;
import java.util.Collection;

/**
 * An entity responsible for loading/saving data from/to cache. It implements a Chain of Responsability pattern,
 * delegating loading and saving operations to {@link DataClassPersistenceManager} and
 * {@link DataClassPersistenceManagerFactory} elements.
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

    @SuppressWarnings("unchecked")
    public < T > DataClassPersistenceManager< T > getDataClassPersistenceManager( Class< T > clazz ) {
        for ( CacheManagerBusElement cacheManagerBusElement : this.cacheManagerBusElementList ) {
            if ( cacheManagerBusElement.canHandleClass( clazz ) ) {
                if ( cacheManagerBusElement instanceof DataClassPersistenceManager< ? > ) {
                    return (DataClassPersistenceManager< T >) cacheManagerBusElement;
                } else if ( cacheManagerBusElement instanceof DataClassPersistenceManagerFactory ) {
                    DataClassPersistenceManagerFactory factory = (DataClassPersistenceManagerFactory) cacheManagerBusElement;
                    return factory.createDataPersistenceManager( clazz );
                }
            }
        }
        throw new IllegalArgumentException( "Class " + clazz.getName() + " is not handled by any registered CacheManagerBusElementList" );
    }
}
