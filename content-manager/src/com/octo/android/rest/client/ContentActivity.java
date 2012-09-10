package com.octo.android.rest.client;

import android.app.Activity;

/**
 * This class is more a sample than a real ready-to-use class. It shows how you can build your base Activity class in
 * your own project. Whatever super class you use (sherlock, fragmentactivity, guice, etc.) you can just copy past the
 * methods below to enable all your activities to use the framework.
 * 
 * @author sni
 * 
 */
public class ContentActivity extends Activity {

    private ContentManager contentManager = new ContentManager( ContentService.class );

    @Override
    protected void onResume() {
        contentManager.start( this );
        super.onResume();
    }

    @Override
    protected void onPause() {
        contentManager.dontNotifyAnyRequestListeners();
        contentManager.shouldStop();
        super.onPause();
    }

    public ContentManager getContentManager() {
        return contentManager;
    }
}