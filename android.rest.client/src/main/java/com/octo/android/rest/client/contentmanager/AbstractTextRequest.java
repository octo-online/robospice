package com.octo.android.rest.client.contentmanager;

import org.springframework.web.client.RestClientException;

import android.os.Bundle;
import android.util.Log;

import com.octo.android.rest.client.webservice.WebService;

public abstract class AbstractTextRequest<ACTIVITY> extends
RestRequest<ACTIVITY, String> {

	private static final long serialVersionUID = -1578679537677496271L;

	private final static String BUNDLE_EXTRA_URL_TEXT = "BUNDLE_EXTRA_URL_TEXT";
	protected String url;

	public AbstractTextRequest(ACTIVITY activity, String url) {
		super(activity, false, false);
		getBundle().putString( BUNDLE_EXTRA_URL_TEXT, url);
		this.url = url;	
	}

	// can't use activity here or any non serializable field
	// will be invoked in remote service
	@Override
	public final String loadDataFromNetwork(WebService webService, Bundle bundle)
			throws RestClientException {
		String url = bundle.getString(BUNDLE_EXTRA_URL_TEXT);
		if (url == null) {
			return null;
		}
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
		return url;
	}

}
