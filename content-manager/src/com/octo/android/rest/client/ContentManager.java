package com.octo.android.rest.client;

import java.util.Collections;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;

import com.octo.android.rest.client.ContentService.ContentServiceBinder;
import com.octo.android.rest.client.persistence.DurationInMillis;
import com.octo.android.rest.client.request.CachedContentRequest;
import com.octo.android.rest.client.request.ContentRequest;
import com.octo.android.rest.client.request.RequestListener;

/**
 * Class used to manage content received from web service. <br/>
 * <ul>
 * <li>Start a {@link Service} to request the web service</li>
 * <li>Manage the communication between the Service and the Activity or Fragment : maintains a list of requests and a
 * list of result receiver (listener)</li>
 * </ul>
 * 
 * @author jva & sni
 * 
 */
public class ContentManager implements Runnable {

    private static final String LOG_TAG = ContentManager.class.getSimpleName();

    private ContentService contentService;
    private ContentServiceConnection contentServiceConnection = new ContentServiceConnection();
    private Context context;

    private boolean isStopped = true;
    private Queue< CachedContentRequest< ? >> requestQueue = new LinkedList< CachedContentRequest< ? >>();
    private Map< CachedContentRequest< ? >, Set< RequestListener< ? >>> mapRequestToRequestListener = Collections
            .synchronizedMap( new IdentityHashMap< CachedContentRequest< ? >, Set< RequestListener< ? >>>() );

    private ExecutorService executorService = Executors.newSingleThreadExecutor();

    // TODO use blocking queue
    private Object lockQueue = new Object();
    // TODO use semaphore
    private Object lockAcquireService = new Object();

    private Thread runner;

    private boolean isUnbinding;

    private Class< ? extends ContentService > contentServiceClass;

    // ============================================================================================
    // THREAD BEHAVIOR
    // ============================================================================================

    public ContentManager( Class< ? extends ContentService > contentServiceClass ) {
        this.contentServiceClass = contentServiceClass;
    }

    public final synchronized void start() {
        throw new IllegalStateException( "Can't be started without context." );
    }

    public synchronized void start( Context context ) {
        this.context = context;
        if ( runner != null ) {
            throw new IllegalStateException( "Already started." );
        } else {
            Intent intentCheck = new Intent( context, contentServiceClass );
            if ( context.getPackageManager().queryIntentServices( intentCheck, 0 ).isEmpty() ) {
                throw new RuntimeException( "Impossible to start content manager as no service of class : " + contentServiceClass.getName()
                        + " is registered in AndroidManifest.xml file !" );
            }
            Log.d( LOG_TAG, "Content manager started." );
            runner = new Thread( this );
            this.isStopped = false;
            runner.start();
        }
    }

    public boolean isStarted() {
        return !isStopped;
    }

    public void run() {
        bindService( context );

        waitForServiceToBeBound();

        while ( !isStopped ) {
            synchronized ( lockQueue ) {
                if ( !requestQueue.isEmpty() ) {
                    CachedContentRequest< ? > restRequest = requestQueue.poll();
                    Set< RequestListener< ? >> listRequestListener = mapRequestToRequestListener.get( restRequest );
                    mapRequestToRequestListener.remove( restRequest );
                    Log.d( LOG_TAG, "Sending request to service : " + restRequest.getClass().getSimpleName() );
                    contentService.addRequest( restRequest, listRequestListener );
                }

                while ( requestQueue.isEmpty() ) {
                    try {
                        lockQueue.wait();
                    } catch ( InterruptedException e ) {
                        e.printStackTrace();
                    }
                }
            }
        }

        unbindService( context );
    }

    // ============================================================================================
    // PUBLIC EXPOSED METHODS
    // ============================================================================================

    /**
     * Call this in {@link Activity#onDestroy} to stop the {@link ContentManager} and all request
     */
    public void shouldStop() {
        if ( this.runner == null ) {
            throw new IllegalStateException( "Not started yet" );
        }
        this.isStopped = true;
        this.runner = null;
        unbindService( context );
        Log.d( LOG_TAG, "Content manager stopped." );
    }

    public void shouldStopAndJoin( long timeOut ) throws InterruptedException {
        if ( this.runner == null ) {
            throw new IllegalStateException( "Not started yet" );
        }
        this.isStopped = true;
        Log.d( LOG_TAG, "Content manager stopped. Joining" );
        unbindService( context );
        this.runner.join( timeOut );
        this.runner = null;
    }

    /**
     * Execute a {@link AsyncTask}, put the result in cache with key <i>requestCacheKey</i> during <i>cacheDuration</i>
     * millisecond and register listeners to notify when request is finished.
     * 
     * @param asyncTask
     *            the AsyncTask to execute
     * @param requestCacheKey
     *            the key used to store and retrieve the result of the request in the cache
     * @param cacheDuration
     *            the time in millisecond to keep cache alive (see {@link DurationInMillis})
     * @param requestListener
     *            the listener to notify when the request will finish
     * @param params
     *            the params of the asynctask to execute
     */
    // TODO get rig of request listener, they should be provided by
    // async task postExecute..
    @TargetApi(3)
    public < Params, Progress, Result > void execute( AsyncTask< Params, Progress, Result > asyncTask, String requestCacheKey, long cacheDuration,
            RequestListener< Result > requestListener, Params... params ) {

        synchronized ( lockQueue ) {
            CachedContentRequest< Result > cachedContentRequest = new CachedContentRequest< Result >( asyncTask, requestCacheKey, cacheDuration, params );
            // add listener to listeners list for this request
            Set< RequestListener< ? >> listeners = mapRequestToRequestListener.get( cachedContentRequest );
            if ( listeners == null ) {
                listeners = new HashSet< RequestListener< ? >>();
                this.mapRequestToRequestListener.put( cachedContentRequest, listeners );
            }
            listeners.add( requestListener );

            this.requestQueue.add( cachedContentRequest );
            lockQueue.notifyAll();
        }
    }

    /**
     * Execute a request, without using cache.
     * 
     * @param request
     *            the request to execute.
     * @param requestListener
     *            the listener to notify when the request will finish.
     */
    public < T > void execute( ContentRequest< T > request, RequestListener< T > requestListener ) {

        synchronized ( lockQueue ) {
            CachedContentRequest< T > cachedContentRequest = new CachedContentRequest< T >( request, null, DurationInMillis.ALWAYS );
            // add listener to listeners list for this request
            Set< RequestListener< ? >> listeners = mapRequestToRequestListener.get( cachedContentRequest );
            if ( listeners == null ) {
                listeners = new HashSet< RequestListener< ? >>();
                this.mapRequestToRequestListener.put( cachedContentRequest, listeners );
            }
            listeners.add( requestListener );

            this.requestQueue.add( cachedContentRequest );
            lockQueue.notifyAll();
        }
    }

    /**
     * Execute a request, put the result in cache with key <i>requestCacheKey</i> during <i>cacheDuration</i>
     * millisecond and register listeners to notify when request is finished.
     * 
     * @param request
     *            the request to execute
     * @param requestCacheKey
     *            the key used to store and retrieve the result of the request in the cache
     * @param cacheDuration
     *            the time in millisecond to keep cache alive (see {@link DurationInMillis})
     * @param requestListener
     *            the listener to notify when the request will finish
     */
    public < T > void execute( ContentRequest< T > request, String requestCacheKey, long cacheDuration, RequestListener< T > requestListener ) {

        synchronized ( lockQueue ) {
            CachedContentRequest< T > cachedContentRequest = new CachedContentRequest< T >( request, requestCacheKey, cacheDuration );
            // add listener to listeners list for this request
            Set< RequestListener< ? >> listeners = mapRequestToRequestListener.get( cachedContentRequest );
            if ( listeners == null ) {
                listeners = new HashSet< RequestListener< ? >>();
                this.mapRequestToRequestListener.put( cachedContentRequest, listeners );
            }
            listeners.add( requestListener );

            this.requestQueue.add( cachedContentRequest );
            lockQueue.notifyAll();
        }
    }

    /**
     * Execute a request, put the result in cache and register listeners to notify when request is finished.
     * 
     * @param request
     *            the request to execute. {@link CachedContentRequest} is a wrapper of {@link ContentRequest} that
     *            contains cache key and cache duration
     * @param requestListener
     *            the listener to notify when the request will finish
     */
    public < T > void execute( CachedContentRequest< T > cachedContentRequest, RequestListener< T > requestListener ) {
        synchronized ( lockQueue ) {
            // add listener to listeners list for this request
            Set< RequestListener< ? >> listeners = mapRequestToRequestListener.get( cachedContentRequest );
            if ( listeners == null ) {
                listeners = new HashSet< RequestListener< ? >>();
            } else if ( !listeners.contains( requestListener ) ) {
                listeners.add( requestListener );
            }
            this.mapRequestToRequestListener.put( cachedContentRequest, listeners );
            this.requestQueue.add( cachedContentRequest );
            lockQueue.notifyAll();
        }
    }

    /**
     * Cancel a specific request
     * 
     * @param request
     *            the request to cancel
     */
    public void cancel( ContentRequest< ? > request ) {
        request.cancel();
    }

    /**
     * Cancel all requests
     */
    public void cancelAllRequests() {
        synchronized ( lockQueue ) {
            for ( ContentRequest< ? > restRequest : requestQueue ) {
                restRequest.cancel();
            }
        }
    }

    /**
     * Remove some specific content from cache
     * 
     * @param clazz
     *            the Type of data you want to remove from cache
     * @param cacheKey
     *            the key of the object in cache
     * @return true if the data has been deleted from cache
     */
    public < T > void removeDataFromCache( final Class< T > clazz, final Object cacheKey ) {
        executorService.execute( new Runnable() {

            public void run() {
                waitForServiceToBeBound();
                contentService.removeDataFromCache( clazz, cacheKey );
            }
        } );
    }

    public void removeAllDataFromCache() {
        executorService.execute( new Runnable() {

            public void run() {
                waitForServiceToBeBound();
                contentService.removeAllDataFromCache();
            }
        } );
    }

    /**
     * Configure the behavior in case of error during reading/writing cache. <br/>
     * Specify wether an error on reading/writing cache must fail the process.
     * 
     * @param failOnCacheError
     *            true if an error must fail the process
     */
    public void setFailOnCacheError( final boolean failOnCacheError ) {
        executorService.execute( new Runnable() {

            public void run() {
                waitForServiceToBeBound();
                contentService.setFailOnCacheError( failOnCacheError );
            }
        } );
    }

    private void waitForServiceToBeBound() {
        synchronized ( lockAcquireService ) {
            while ( contentService == null && !isStopped ) {
                try {
                    lockAcquireService.wait();
                } catch ( InterruptedException e ) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Disable request listeners notifications for a specific request.<br/>
     * All listeners associated to this request won't be called when request will finish.<br/>
     * Should be called in {@link Activity#onPause}
     * 
     * @param request
     *            Request on which you want to disable listeners
     */
    public void dontNotifyRequestListenersForRequest( final ContentRequest< ? > request ) {
        synchronized ( lockQueue ) {
            // normally a list iterator would be better suited here
            // but we exit the loop after first removal, so it's not needed
            for ( CachedContentRequest< ? > cachedContentRequest : mapRequestToRequestListener.keySet() ) {
                if ( cachedContentRequest.getContentRequest().equals( request ) ) {
                    dontNotifyRequestListenersForRequestInternal( request, cachedContentRequest );
                    mapRequestToRequestListener.remove( cachedContentRequest );
                    break;
                }
            }
        }
    }

    /**
     * Disable request listeners notifications for all requests. <br/>
     * Should be called in {@link Activity#onPause}
     */
    public void dontNotifyAnyRequestListeners() {
        synchronized ( lockQueue ) {
            for ( CachedContentRequest< ? > cachedContentRequest : mapRequestToRequestListener.keySet() ) {
                final ContentRequest< ? > request = cachedContentRequest.getContentRequest();
                dontNotifyRequestListenersForRequestInternal( request, cachedContentRequest );
            }
            mapRequestToRequestListener.clear();
        }
    }

    protected void dontNotifyRequestListenersForRequestInternal( final ContentRequest< ? > request, CachedContentRequest< ? > cachedContentRequest ) {
        final Set< RequestListener< ? >> setRequestListeners = mapRequestToRequestListener.get( cachedContentRequest );
        executorService.execute( new Runnable() {

            public void run() {
                waitForServiceToBeBound();
                contentService.dontNotifyRequestListenersForRequest( request, setRequestListeners );
            }
        } );
    }

    // ============================================================================================
    // INNER CLASS
    // ============================================================================================
    public class ContentServiceConnection implements ServiceConnection {

        public void onServiceConnected( ComponentName name, IBinder service ) {
            synchronized ( this ) {
                contentService = ( (ContentServiceBinder) service ).getContentService();
                Log.d( LOG_TAG, "Bound to service : " + contentService.getClass().getSimpleName() );
            }
            synchronized ( lockAcquireService ) {
                lockAcquireService.notifyAll();
            }
        }

        public void onServiceDisconnected( ComponentName name ) {
            synchronized ( this ) {
                contentService = null;
                isUnbinding = false;
            }

            synchronized ( lockAcquireService ) {
                lockAcquireService.notifyAll();
            }
        }
    }

    // ============================================================================================
    // PRIVATE
    // ============================================================================================

    private void bindService( Context context ) {
        Intent intentService = new Intent( context, contentServiceClass );
        Log.d( LOG_TAG, "Binding to service." );
        contentServiceConnection = new ContentServiceConnection();
        context.bindService( intentService, contentServiceConnection, Context.BIND_AUTO_CREATE );
    }

    private void unbindService( Context context ) {
        synchronized ( this ) {
            if ( contentService != null && !isUnbinding ) {
                isUnbinding = true;
                context.unbindService( this.contentServiceConnection );
            }
        }
    }
}