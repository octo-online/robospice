package com.octo.android.rest.client.contentmanager;

import org.springframework.web.client.RestClientException;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.octo.android.rest.client.webservice.WebService;

public abstract class AbstractTextRequest extends RestRequest<String> {

	private String url;

	public AbstractTextRequest(Context context, String url) {
		super(context, String.class);
		this.url = url;	
	}

	// can't use activity here or any non serializable field
	// will be invoked in remote service
	@Override
	public final String loadDataFromNetwork(WebService webService, Bundle bundle)
			throws RestClientException {
		Log.d(getClass().getName(), "Call web service " + url);
		return webService.getRestTemplate().getForObject(url, String.class);
	}

	// can't use activity here or any non serializable field
	// will be invoked in remote service
	protected final String getUrl() {
		return this.url;
	}

	@Override
	public final String getCacheKey() {
		return url.replace(":", "").replace("/", "_");
	}

}
