package com.octo.android.rest.client.persistence;

import android.app.Application;

public abstract class ClassCacheManagerFactory {

    private Application mApplication;

    public ClassCacheManagerFactory( Application application ) {
        this.mApplication = application;
    }

    protected final Application getApplication() {
        return mApplication;
    }

    /**
     * Wether or not this bus element can persist/unpersist objects of the given class clazz.
     * 
     * @param clazz
     *            the class of objets we are looking forward to persist.
     * @return true if this bus element can persist/unpersist objects of the given class clazz. False otherwise.
     */
    public abstract boolean canHandleClass( Class< ? > clazz );

    public abstract < DATA > ClassCacheManager< DATA > createClassCacheManager( Class< DATA > clazz );

    public abstract void removeAllDataFromCache();
}
