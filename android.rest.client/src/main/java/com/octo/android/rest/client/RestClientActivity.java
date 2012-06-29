package com.octo.android.rest.client;

import android.os.Bundle;

import com.google.inject.Inject;
import com.octo.android.rest.client.contentmanager.ContentManager;
import com.octo.android.rest.client.contentmanager.RestRequest;

import roboguice.activity.RoboActivity;

public class RestClientActivity extends RoboActivity {

	@Inject
	private ContentManager contentManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		contentManager.start(this);
	}
	
	
	public void addRequestToQueue(RestRequest<?> request) {
		contentManager.addRequestToQueue(request);
	}


	@Override
	protected void onPause() {
		contentManager.cancelAllRequests();
		super.onPause();
	}
}