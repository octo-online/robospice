package com.octo.android.rest.client;

import com.octo.android.rest.client.request.CachedContentRequest;

public interface ContentServiceListener {
	public void onRequestProcessed(CachedContentRequest<?> cachedContentRequest);
}
