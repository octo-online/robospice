package com.octo.android.rest.client;

import android.os.Bundle;

import com.google.inject.Inject;
import com.octo.android.rest.client.request.simple.CachedRestRequest;

import roboguice.activity.RoboActivity;

public class ContentActivity extends RoboActivity {

	@Inject
	private ContentManager contentManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		contentManager.start(this);
	}
	
	
	public void addRequestToQueue(CachedRestRequest<?> request, boolean useCache) {
		contentManager.addRequestToQueue(request,useCache);
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