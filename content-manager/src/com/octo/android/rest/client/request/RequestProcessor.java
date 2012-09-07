package com.octo.android.rest.client.request;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.octo.android.rest.client.ContentService;
import com.octo.android.rest.client.exception.CacheLoadingException;
import com.octo.android.rest.client.exception.CacheSavingException;
import com.octo.android.rest.client.exception.ContentManagerException;
import com.octo.android.rest.client.exception.NetworkException;
import com.octo.android.rest.client.exception.NoNetworkException;
import com.octo.android.rest.client.persistence.ICacheManager;

/**
 * Delegate class of the {@link ContentService}, easier to test than an Android {@link Service}. TODO make it possible
 * to set the number of threads in the {@link ExecutorService}
 * 
 * @author jva
 * 
 */
public class RequestProcessor {
    // ============================================================================================
    // CONSTANT
    // ============================================================================================
    private final static String LOG_CAT = "RequestProcessor";

    // ============================================================================================
    // ATTRIBUTES
    // ============================================================================================
    private Map< CachedContentRequest< ? >, Set< RequestListener< ? >>> mapRequestToRequestListener = Collections
            .synchronizedMap( new IdentityHashMap< CachedContentRequest< ? >, Set< RequestListener< ? >>>() );

    /**
     * Thanks Olivier Croiser from Zenika for his excellent <a
     * href="http://blog.zenika.com/index.php?post/2012/04/11/Introduction-programmation-concurrente-Java-2sur2. ">blog
     * article</a>.
     */
    private ExecutorService executorService = null;

    private ICacheManager cacheManager;

    private Handler handlerResponse;

    private Context applicationContext;

    private boolean failOnCacheError;

    // ============================================================================================
    // CONSTRUCTOR
    // ============================================================================================

    public RequestProcessor( Context context, ICacheManager cacheManager, int threadCount ) {
        this.applicationContext = context;
        this.cacheManager = cacheManager;
        handlerResponse = new Handler( Looper.getMainLooper() );
        if ( threadCount <= 0 ) {
            throw new IllegalArgumentException( "Thread count must be >= 1" );
        } else if ( threadCount == 1 ) {
            executorService = Executors.newSingleThreadExecutor();
        } else {
            Executors.newFixedThreadPool( threadCount );
        }
    }

    // ============================================================================================
    // PUBLIC
    // ============================================================================================
    public void addRequest( final CachedContentRequest< ? > request, Set< RequestListener< ? >> listRequestListener ) {
        Log.d( LOG_CAT, "Adding request to queue : " + request );

        if ( listRequestListener != null ) {
            Set< RequestListener< ? >> listRequestListenerForThisRequest = mapRequestToRequestListener.get( request );

            if ( listRequestListenerForThisRequest == null ) {
                listRequestListenerForThisRequest = new HashSet< RequestListener< ? >>();
                this.mapRequestToRequestListener.put( request, listRequestListenerForThisRequest );
            }

            listRequestListenerForThisRequest.addAll( listRequestListener );
        }

        executorService.execute( new Runnable() {
            public void run() {
                processRequest( request );
            }
        } );
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    protected < T > void processRequest( CachedContentRequest< T > request ) {
        Log.d( LOG_CAT, "Processing request : " + request );

        if ( request.isCancelled() ) {
            Log.d( LOG_CAT, "Not processing request : " + request + " as it is cancelled. " );
            return;
        }

        T result = null;
        Set< RequestListener< ? >> requestListeners = mapRequestToRequestListener.get( request );

        if ( request.getRequestCacheKey() != null ) {
            // First, search data in cache
            try {
                Log.d( LOG_CAT, "Loading request from cache : " + request );
                result = loadDataFromCache( request.getResultType(), request.getRequestCacheKey(), request.getCacheDuration() );
            } catch ( CacheLoadingException e ) {
                Log.d( getClass().getName(), "Cache file could not be read.", e );
                if ( failOnCacheError ) {
                    handlerResponse.post( new ResultRunnable( requestListeners, e ) );
                    return;
                }
            }
        }

        if ( result == null && !request.isCancelled() ) {
            // if file is not found or the date is a day after or cache disabled, call the web service
            Log.d( LOG_CAT, "Cache content not available or expired or disabled" );
            if ( !isNetworkAvailable( applicationContext ) ) {
                Log.e( LOG_CAT, "Network is down." );
                handlerResponse.post( new ResultRunnable( requestListeners, new NoNetworkException() ) );
                return;
            } else {
                // Nothing found in cache (or cache expired), load from network
                try {
                    result = request.loadDataFromNetwork();
                    if ( result == null ) {
                        Log.d( LOG_CAT, "Unable to get web service result : " + request.getResultType() );
                        handlerResponse.post( new ResultRunnable( requestListeners, (T) null ) );
                        return;
                    }
                } catch ( Exception e ) {
                    Log.e( LOG_CAT, "A rest client exception occured during service execution :" + e.getMessage(), e );
                    handlerResponse
                            .post( new ResultRunnable( requestListeners, new NetworkException( "Exception occured during invocation of web service.", e ) ) );
                    return;
                }

                if ( request.getRequestCacheKey() != null ) {
                    // request worked and result is not null
                    try {
                        Log.d( LOG_CAT, "Start caching content..." );
                        result = saveDataToCacheAndReturnData( result, request.getRequestCacheKey() );
                        handlerResponse.post( new ResultRunnable( requestListeners, result ) );
                        return;
                    } catch ( CacheSavingException e ) {
                        Log.d( LOG_CAT, "An exception occured during service execution :" + e.getMessage(), e );
                        if ( failOnCacheError ) {
                            handlerResponse.post( new ResultRunnable( requestListeners, e ) );
                            return;
                        }
                    }
                }
            }
        }
        // we reached that point so write in cache didn't work but network worked.
        handlerResponse.post( new ResultRunnable( requestListeners, result ) );
    }

    /**
     * Disable request listeners notifications for a specific request.<br/>
     * All listeners associated to this request won't be called when request will finish.<br/>
     * Should be called in {@link Activity#onPause}
     * 
     * @param request
     *            Request on which you want to disable listeners
     * @param listRequestListener
     *            the collection of listeners associated to request not to be notified
     */
    public void dontNotifyRequestListenersForRequest( ContentRequest< ? > request, Collection< RequestListener< ? >> listRequestListener ) {
        for ( CachedContentRequest< ? > cachedContentRequest : mapRequestToRequestListener.keySet() ) {
            if ( cachedContentRequest.getContentRequest().equals( request ) ) {
                Set< RequestListener< ? >> setRequestListener = mapRequestToRequestListener.get( cachedContentRequest );
                if ( setRequestListener != null && listRequestListener != null ) {
                    setRequestListener.removeAll( listRequestListener );
                }
                break;
            }
        }
    }

    /**
     * @return true if network is available (at least one way to connect to network is connected or connecting).
     */
    public boolean isNetworkAvailable( Context context ) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService( Context.CONNECTIVITY_SERVICE );
        NetworkInfo[] allNetworkInfos = connectivityManager.getAllNetworkInfo();
        for ( NetworkInfo networkInfo : allNetworkInfos ) {
            if ( networkInfo.getState() == NetworkInfo.State.CONNECTED || networkInfo.getState() == NetworkInfo.State.CONNECTING ) {
                return true;
            }
        }
        return false;
    }

    public boolean removeDataFromCache( Class< ? > clazz, Object cacheKey ) {
        return cacheManager.removeDataFromCache( clazz, cacheKey );
    }

    public void removeAllDataFromCache( Class< ? > clazz ) {
        cacheManager.removeAllDataFromCache( clazz );
    }

    public void removeAllDataFromCache() {
        cacheManager.removeAllDataFromCache();
    }

    public boolean isFailOnCacheError() {
        return failOnCacheError;
    }

    public void setFailOnCacheError( boolean failOnCacheError ) {
        this.failOnCacheError = failOnCacheError;
    }

    // ============================================================================================
    // PRIVATE
    // ============================================================================================

    private < T > T loadDataFromCache( Class< T > clazz, Object cacheKey, long maxTimeInCacheBeforeExpiry ) throws CacheLoadingException {
        return cacheManager.loadDataFromCache( clazz, cacheKey, maxTimeInCacheBeforeExpiry );
    }

    private < T > T saveDataToCacheAndReturnData( T data, Object cacheKey ) throws CacheSavingException {
        return cacheManager.saveDataToCacheAndReturnData( data, cacheKey );
    }

    private class ResultRunnable< T > implements Runnable {

        private ContentManagerException contentManagerException;
        private T result;
        private Set< RequestListener< T >> listeners;

        public ResultRunnable( Set< RequestListener< T >> listeners, T result ) {
            this.result = result;
            this.listeners = listeners;
        }

        public ResultRunnable( Set< RequestListener< T >> listeners, ContentManagerException contentManagerException ) {
            this.listeners = listeners;
            this.contentManagerException = contentManagerException;
        }

        public void run() {
            if ( listeners == null ) {
                return;
            }

            for ( RequestListener< T > listener : listeners ) {
                if ( contentManagerException == null ) {
                    listener.onRequestSuccess( result );
                } else {
                    listener.onRequestFailure( contentManagerException );
                }
            }
        }
    }
}
