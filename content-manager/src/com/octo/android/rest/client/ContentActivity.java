package com.octo.android.rest.client;

import android.app.Activity;
import android.os.Bundle;

import com.octo.android.rest.client.request.CachedContentRequest;
import com.octo.android.rest.client.request.ContentRequest;
import com.octo.android.rest.client.request.RequestListener;

/**
 * This class is more a sample than a real ready-to-use class. It shows how you can build your base Activity class in
 * your own project. Whatever super class you use (sherlock, fragmentactivity, guice, etc.) you can just copy past the
 * methods below to enable all your activities to use the framework.
 * 
 * @author sni
 * 
 */
public class ContentActivity extends Activity {

    private ContentManager contentManager = new ContentManager();

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        contentManager.start( this );
    }

    public < T > void execute( ContentRequest< T > request, String requestCacheKey, long cacheDuration, RequestListener< T > requestListener ) {
        contentManager.execute( request, requestCacheKey, cacheDuration, requestListener );
    }

    public < T > void execute( CachedContentRequest< T > cachedContentRequest, RequestListener< T > requestListener ) {
        contentManager.execute( cachedContentRequest, requestListener );
    }

    @Override
    protected void onPause() {
        contentManager.cancelAllRequests();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        contentManager.shouldStop();
        super.onDestroy();
    }

}