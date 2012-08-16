package com.octo.android.rest.client.persistence;

/**
 * Defines the behavior of a all elements in the chain of responsability of the {@link CacheManager}.
 * 
 * @author sni
 * 
 */
public interface CacheManagerBusElement {
    /**
     * Wether or not this bus element can persist/unpersist objects of the given class clazz.
     * 
     * @param clazz
     *            the class of objets we are looking forward to persist.
     * @return true if this bus element can persist/unpersist objects of the given class clazz. False otherwise.
     */
    public boolean canHandleClass( Class< ? > clazz );

}
