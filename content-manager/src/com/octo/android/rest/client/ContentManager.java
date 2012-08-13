package com.octo.android.rest.client;

import java.util.Collections;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.octo.android.rest.client.ContentService.ContentServiceBinder;
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
 * @author jva
 * 
 */
public class ContentManager extends Thread {

    private ContentService contentService;
    private ContentServiceConnection contentServiceConnection = new ContentServiceConnection();
    private Context context;

    private boolean isStopped;
    private Queue< CachedContentRequest< ? >> requestQueue = new LinkedList< CachedContentRequest< ? >>();
    private Map< CachedContentRequest< ? >, Set< RequestListener< ? >>> mapRequestToRequestListener = Collections
            .synchronizedMap( new IdentityHashMap< CachedContentRequest< ? >, Set< RequestListener< ? >>>() );

    // TODO use blocking queue
    private Object lockQueue = new Object();
    // TODO use semaphore
    private Object lockAcquireService = new Object();
    private boolean contentServiceMustBeSetFailOnErrorAsap = false;

    @Override
    public final synchronized void start() {
        throw new IllegalStateException( "Can't be started without context." );
    }

    public synchronized void start( Context context ) {
        this.context = context;
        super.start();
    }

    @Override
    public void run() {
        bindService( context );

        synchronized ( lockAcquireService ) {
            while ( contentService == null ) {
                try {
                    lockAcquireService.wait();
                } catch ( InterruptedException e ) {
                    e.printStackTrace();
                }
            }
        }

        contentService.setFailOnCacheError( this.contentServiceMustBeSetFailOnErrorAsap );

        while ( !isStopped ) {
            synchronized ( lockQueue ) {
                if ( !requestQueue.isEmpty() ) {
                    CachedContentRequest< ? > restRequest = requestQueue.poll();
                    Set< RequestListener< ? >> listRequestListener = mapRequestToRequestListener.get( restRequest );
                    mapRequestToRequestListener.remove( restRequest );
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

    public void shouldStop() {
        this.isStopped = true;
        unbindService( context );
    }

    private void bindService( Context context ) {
        Intent intentService = new Intent();
        intentService.setAction( "com.octo.android.rest.client.ContentService" );
        contentServiceConnection = new ContentServiceConnection();
        context.bindService( intentService, contentServiceConnection, Context.BIND_AUTO_CREATE );
    }

    private void unbindService( Context context ) {
        if ( contentService != null ) {
            context.unbindService( this.contentServiceConnection );
        }
    }

    public < T > void execute( ContentRequest< T > request, String requestCacheKey, long cacheDuration, RequestListener< T > requestListener ) {
        synchronized ( lockQueue ) {
            CachedContentRequest< T > cachedContentRequest = new CachedContentRequest< T >( request, requestCacheKey, cacheDuration );
            // add listener to listeners list for this request
            Set< RequestListener< ? >> listeners = mapRequestToRequestListener.get( request );
            if ( listeners == null ) {
                listeners = new HashSet< RequestListener< ? >>();
                this.mapRequestToRequestListener.put( cachedContentRequest, listeners );
            }
            listeners.add( requestListener );

            this.requestQueue.add( cachedContentRequest );
            lockQueue.notifyAll();
        }
    }

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

    public void cancel( ContentRequest< ? > request ) {
        request.cancel();
    }

    public class ContentServiceConnection implements ServiceConnection {

        public void onServiceConnected( ComponentName name, IBinder service ) {
            contentService = ( (ContentServiceBinder) service ).getContentService();
            synchronized ( lockAcquireService ) {
                lockAcquireService.notifyAll();
            }
        }

        public void onServiceDisconnected( ComponentName name ) {
            contentService = null;
        }
    }

    public void setFailOnCacheError( boolean failOnCacheError ) {
        this.contentServiceMustBeSetFailOnErrorAsap = failOnCacheError;

        if ( contentService != null ) {
            contentService.setFailOnCacheError( failOnCacheError );
        }
    }

    public void cancelAllRequests() {
        synchronized ( lockQueue ) {
            for ( ContentRequest< ? > restRequest : requestQueue ) {
                restRequest.cancel();
            }
        }
    }

}