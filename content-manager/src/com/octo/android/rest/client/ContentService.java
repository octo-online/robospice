package com.octo.android.rest.client;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.app.Application;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import com.octo.android.rest.client.exception.ContentManagerException;
import com.octo.android.rest.client.exception.LoadFromCacheException;
import com.octo.android.rest.client.exception.NetworkException;
import com.octo.android.rest.client.exception.NoNetworkException;
import com.octo.android.rest.client.exception.SaveToCacheException;
import com.octo.android.rest.client.persistence.CacheExpiredException;
import com.octo.android.rest.client.persistence.DataClassPersistenceManager;
import com.octo.android.rest.client.persistence.CacheManager;
import com.octo.android.rest.client.request.CachedContentRequest;
import com.octo.android.rest.client.request.RequestListener;

/**
 * This is an abstract class used to manage the cache and provide web service result to an activity. <br/>
 * 
 * Extends this class to provide a service able to load content from web service or cache (if available and enabled)
 * 
 * @author jva
 */
public abstract class ContentService extends Service {

    private final static String LOG_CAT = "ContentService";

    // ============================================================================================
    // CONSTANTS
    // ============================================================================================

    public static final int RESULT_OK = 0;
    public static final int RESULT_ERROR = -1;

    protected static final String FILE_CACHE_EXTENSION = ".store";
    private static final String LOGCAT_TAG = "AbstractContentService";

    // ============================================================================================
    // ATTRIBUTES
    // ============================================================================================

    private boolean failOnCacheError = false;

    public ContentServiceBinder mContentServiceBinder;
    /**
     * Thanks Olivier Croiser from Zenika for his excellent <a
     * href="http://blog.zenika.com/index.php?post/2012/04/11/Introduction-programmation-concurrente-Java-2sur2. ">blog
     * article</a>.
     */
    private ExecutorService executorService = Executors.newSingleThreadExecutor();

    /** Responsible for persisting data. */
    private CacheManager dataPersistenceManager;
    /** Used to post results on the UI thread of the activity. */
    private Handler handlerResponse;

    private Map< CachedContentRequest< ? >, Set< RequestListener< ? >>> mapRequestToRequestListener = Collections
            .synchronizedMap( new IdentityHashMap< CachedContentRequest< ? >, Set< RequestListener< ? >>>() );

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
        dataPersistenceManager = createDataPersistenceManager( getApplication() );
        handlerResponse = new Handler( Looper.getMainLooper() );
        Log.d( LOG_CAT, "Content Service instance created." );
    }

    // ============================================================================================
    // METHODS
    // ============================================================================================

    public abstract CacheManager createDataPersistenceManager( Application application );

    public void addRequest( final CachedContentRequest< ? > request, Set< RequestListener< ? >> listRequestListener ) {
        Log.d( LOG_CAT, "Adding request to queue : " + request );

        Set< RequestListener< ? >> listRequestListenerForThisRequest = mapRequestToRequestListener.get( request );

        if ( listRequestListenerForThisRequest == null ) {
            listRequestListenerForThisRequest = new HashSet< RequestListener< ? >>();
            this.mapRequestToRequestListener.put( request, listRequestListenerForThisRequest );
        }

        listRequestListenerForThisRequest.addAll( listRequestListener );

        executorService.execute( new Runnable() {
            public void run() {
                processRequest( request );
            }
        } );
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    protected < T > void processRequest( CachedContentRequest< T > request ) {
        Log.d( LOG_CAT, "Processing request : " + request );

        T result = null;
        Set< RequestListener< ? >> requestListeners = mapRequestToRequestListener.get( request );
        try {
            Log.d( LOG_CAT, "Loading request from cache : " + request );
            result = loadDataFromCache( request.getResultType(), request.getRequestCacheKey(), request.getCacheDuration() );
        } catch ( FileNotFoundException e ) {
            Log.d( getClass().getName(), "Cache file not found.", e );
            if ( failOnCacheError ) {
                handlerResponse.post( new ResultRunnable( requestListeners, new LoadFromCacheException( "Cache file not found.", e ) ) );
                return;
            }
        } catch ( IOException e ) {
            Log.d( getClass().getName(), "Cache file could not be read.", e );
            if ( failOnCacheError ) {
                handlerResponse.post( new ResultRunnable( requestListeners, new LoadFromCacheException( "Cache file could not be read.", e ) ) );
                return;
            }
        } catch ( CacheExpiredException e ) {
            Log.d( getClass().getName(), "Cache file has expired.", e );
            if ( failOnCacheError ) {
                handlerResponse.post( new ResultRunnable( requestListeners, new LoadFromCacheException( "Cache file has expired.", e ) ) );
                return;
            }
        }

        if ( result == null && !request.isCanceled() ) {
            // if file is not found or the date is a day after or cache disabled, call the web service
            Log.d( LOGCAT_TAG, "Cache content not available or expired or disabled" );
            if ( !isNetworkAvailable( getApplicationContext() ) ) {
                Log.e( LOGCAT_TAG, "Network is down." );
                handlerResponse.post( new ResultRunnable( requestListeners, new NoNetworkException() ) );
                return;
            } else {
                try {
                    result = request.loadDataFromNetwork();
                    if ( result == null ) {
                        Log.d( LOGCAT_TAG, "Unable to get web service result : " + request.getResultType() );
                        handlerResponse.post( new ResultRunnable( requestListeners, (T) null ) );
                        return;
                    }
                } catch ( Exception e ) {
                    Log.e( LOGCAT_TAG, "A rest client exception occured during service execution :" + e.getMessage(), e );
                    handlerResponse
                            .post( new ResultRunnable( requestListeners, new NetworkException( "Exception occured during invocation of web service.", e ) ) );
                    return;
                }

                // request worked and result is not null
                try {
                    Log.d( LOGCAT_TAG, "Start caching content..." );
                    result = saveDataToCacheAndReturnData( result, request.getRequestCacheKey() );
                    handlerResponse.post( new ResultRunnable( requestListeners, result ) );
                    return;
                } catch ( FileNotFoundException e ) {
                    Log.d( LOGCAT_TAG, "A file not found exception occured during service execution :" + e.getMessage(), e );
                    if ( failOnCacheError ) {
                        handlerResponse.post( new ResultRunnable( requestListeners, new SaveToCacheException(
                                "A file not found exception occured during service execution :", e ) ) );
                        return;
                    }
                } catch ( IOException e ) {
                    Log.d( LOGCAT_TAG, "An io exception occured during service execution :" + e.getMessage(), e );
                    if ( failOnCacheError ) {
                        handlerResponse.post( new ResultRunnable( requestListeners, new SaveToCacheException(
                                "An io exception occured during service execution :", e ) ) );
                        return;
                    }
                }
            }
        }
        // we reached that point so write in cache didn't work but network worked.
        handlerResponse.post( new ResultRunnable( requestListeners, result ) );
    }

    public < T > T loadDataFromCache( Class< T > clazz, Object cacheKey, long maxTimeInCacheBeforeExpiry ) throws FileNotFoundException, IOException,
            CacheExpiredException {
        return dataPersistenceManager.getDataClassPersistenceManager( clazz ).loadDataFromCache( cacheKey, maxTimeInCacheBeforeExpiry );
    }

    @SuppressWarnings("unchecked")
    public < T > T saveDataToCacheAndReturnData( T data, Object cacheKey ) throws FileNotFoundException, IOException {
        DataClassPersistenceManager< T > dataClassPersistenceManager = (DataClassPersistenceManager< T >) dataPersistenceManager
                .getDataClassPersistenceManager( data.getClass() );
        return dataClassPersistenceManager.saveDataToCacheAndReturnData( data, cacheKey );
    }

    public boolean isFailOnCacheError() {
        return failOnCacheError;
    }

    public void setFailOnCacheError( boolean failOnCacheError ) {
        this.failOnCacheError = failOnCacheError;
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
            for ( RequestListener< T > listener : listeners ) {
                if ( contentManagerException == null ) {
                    listener.onRequestSuccess( result );
                } else {
                    listener.onRequestFailure( contentManagerException );
                }
            }
        }
    }

    /**
     * @return true if network is available (at least one way to connect to network is connected or connecting).
     */
    public static boolean isNetworkAvailable( Context context ) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService( Context.CONNECTIVITY_SERVICE );
        NetworkInfo[] allNetworkInfos = connectivityManager.getAllNetworkInfo();
        for ( NetworkInfo networkInfo : allNetworkInfos ) {
            if ( networkInfo.getState() == NetworkInfo.State.CONNECTED || networkInfo.getState() == NetworkInfo.State.CONNECTING ) {
                return true;
            }
        }
        return false;
    }

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