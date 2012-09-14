package com.octo.android.rest.client;

import java.util.Collection;
import java.util.Set;

import android.app.Application;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.octo.android.rest.client.persistence.CacheManager;
import com.octo.android.rest.client.request.CachedContentRequest;
import com.octo.android.rest.client.request.ContentRequest;
import com.octo.android.rest.client.request.RequestListener;
import com.octo.android.rest.client.request.RequestProcessor;

/**
 * This is an abstract class used to manage the cache and provide web service result to an activity. <br/>
 * 
 * Extends this class to provide a service able to load content from web service or cache (if available and enabled).
 * You will have to implement {@link #createCacheManager(Application)} to configure the {@link CacheManager} used by all
 * requests to persist their results in the cache (and load them from cache if possible).
 * 
 * @author jva
 * @author sni
 */
public class ContentService extends Service {

    private static final int DEFAULT_THREAD_COUNT = 4;

    private final static String LOG_CAT = "ContentService";

    // ============================================================================================
    // ATTRIBUTES
    // ============================================================================================

    // ============================================================================================
    // ATTRIBUTES
    // ============================================================================================
    public ContentServiceBinder mContentServiceBinder;

    /** Responsible for persisting data. */
    private CacheManager cacheManager;

    private RequestProcessor requestProcessor;

    // ============================================================================================
    // CONSTRUCTOR
    // ============================================================================================
    /**
     * Basic constructor
     * 
     * @param name
     */
    public ContentService() {
        mContentServiceBinder = new ContentServiceBinder();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if ( !( getApplication() instanceof ContentConfiguration ) ) {
            throw new RuntimeException( "Application class :" + getApplication().getClass().getName() + " doesn't implement "
                    + ContentConfiguration.class.getName() );
        }
        cacheManager = ( (ContentConfiguration) getApplication() ).getCacheManager();
        requestProcessor = new RequestProcessor( getApplicationContext(), cacheManager, getThreadCount() );

        Log.d( LOG_CAT, "Content Service instance created." );
    }

    /**
     * Override this method to set the number of threads used to execute requests.
     * 
     * @return the number of threads to use when executing requests.
     */
    public int getThreadCount() {
        return DEFAULT_THREAD_COUNT;
    }

    // ============================================================================================
    // DELEGATE METHODS (to ease tests)
    // ============================================================================================

    public void addRequest( final CachedContentRequest< ? > request, Set< RequestListener< ? >> listRequestListener ) {
        requestProcessor.addRequest( request, listRequestListener );
    }

    public boolean removeDataFromCache( Class< ? > clazz, Object cacheKey ) {
        return requestProcessor.removeDataFromCache( clazz, cacheKey );
    }

    public void removeAllDataFromCache( Class< ? > clazz ) {
        requestProcessor.removeAllDataFromCache( clazz );
    }

    public void removeAllDataFromCache() {
        requestProcessor.removeAllDataFromCache();
    }

    public boolean isFailOnCacheError() {
        return requestProcessor.isFailOnCacheError();
    }

    public void setFailOnCacheError( boolean failOnCacheError ) {
        requestProcessor.setFailOnCacheError( failOnCacheError );
    }

    public void dontNotifyRequestListenersForRequest( ContentRequest< ? > request, Collection< RequestListener< ? >> listRequestListener ) {
        requestProcessor.dontNotifyRequestListenersForRequest( request, listRequestListener );
    }

    public void cancellAllPendingRequests() {
        requestProcessor.cancellAllPendingRequests();
    }

    // ============================================================================================
    // SERVICE METHODS
    // ============================================================================================

    @Override
    public IBinder onBind( Intent intent ) {
        return mContentServiceBinder;
    }

    public class ContentServiceBinder extends Binder {
        public ContentService getContentService() {
            return ContentService.this;
        }
    }
}