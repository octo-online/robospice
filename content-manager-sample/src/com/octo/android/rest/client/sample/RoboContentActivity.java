package com.octo.android.rest.client.sample;

import roboguice.activity.RoboActivity;
import android.os.Bundle;

import com.octo.android.rest.client.ContentManager;
import com.octo.android.rest.client.request.CachedContentRequest;
import com.octo.android.rest.client.request.ContentRequest;
import com.octo.android.rest.client.request.RequestListener;

public class RoboContentActivity extends RoboActivity {
	private ContentManager contentManager = new ContentManager();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		contentManager.start(this);
	}

	public <T> void execute(ContentRequest<T> request, String requestCacheKey, long cacheDuration, RequestListener<T> requestListener) {
		contentManager.execute(request, requestCacheKey, cacheDuration, requestListener);
	}

	public <T> void execute(CachedContentRequest<T> cachedContentRequest, RequestListener<T> requestListener) {
		contentManager.execute(cachedContentRequest, requestListener);
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
}
