package com.octo.android.rest.client;

import android.app.Activity;
import android.os.Bundle;

import com.octo.android.rest.client.request.ContentRequest;
import com.octo.android.rest.client.request.RequestListener;

public class ContentActivity extends Activity {

	private ContentManager contentManager = new ContentManager();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		contentManager.start(this);
	}

	public void addRequestToQueue(ContentRequest<?> request, boolean useCache) {
		contentManager.addRequestToQueue(request, new RequestListener() {

			public void onRequestFailure(int resultCode) {

			}

			public void onRequestSuccess(Object result) {

			}
		}, useCache);
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