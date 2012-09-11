package com.octo.android.rest.client;

import android.app.Activity;

/**
 * This class is more a sample than a real ready-to-use class. It shows how you can build your base Activity class in
 * your own project. Whatever super class you use (sherlock, fragmentactivity, guice, etc.) you can just copy past the
 * methods below to enable all your activities to use the framework.
 * 
 * The binding can take place, at best during <a
 * href="http://stackoverflow.com/questions/2304086/binding-to-service-in-oncreate-or-in-onresume">certain lifecycle
 * operations </a>: {@link #onStart()} and {@link #onStop()}.
 * 
 * @author sni
 * 
 */
public class ContentActivity extends Activity {

    private ContentManager contentManager = new ContentManager( ContentService.class );

    @Override
    protected void onStart() {
        contentManager.start( this );
        super.onStart();
    }

    @Override
    protected void onStop() {
        contentManager.shouldStop();
        super.onStop();
    }

    public ContentManager getContentManager() {
        return contentManager;
    }
}