package com.octo.android.rest.client.sample;

import roboguice.activity.RoboActivity;
import android.os.Bundle;

import com.octo.android.rest.client.ContentManager;

/**
 * This class is the base class of all activities of the sample project.
 * 
 * Typically, in a new project, you will have to create a base class like this one and copy the content of the
 * {@link ContentActivity} into your own class.
 * 
 * @author sni
 * 
 */
public class ContentActivity extends RoboActivity {
    private ContentManager contentManager = new ContentManager( "com.octo.android.rest.client.ContentService" );

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        contentManager.start( this );
    }

    @Override
    protected void onPause() {
        // either avoid notify listeners (requests will finish but no listener will be notified)
        // either cancel requests
        contentManager.dontNotifyAnyRequestListeners();
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
