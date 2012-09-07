package com.octo.android.rest.client;

import android.app.Activity;
import android.os.Bundle;

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

    public ContentManager getContentManager() {
        return contentManager;
    }
}