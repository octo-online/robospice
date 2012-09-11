package com.octo.android.rest.client.sample;

import roboguice.activity.RoboActivity;

import com.octo.android.rest.client.ContentManager;
import com.octo.android.rest.client.SpringAndroidContentService;

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
    private ContentManager contentManager = new ContentManager( SpringAndroidContentService.class );

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
