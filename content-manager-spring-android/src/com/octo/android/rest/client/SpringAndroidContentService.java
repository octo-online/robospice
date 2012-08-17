package com.octo.android.rest.client;

import java.util.Set;

import org.springframework.web.client.RestTemplate;

import com.octo.android.rest.client.request.CachedContentRequest;
import com.octo.android.rest.client.request.RequestListener;
import com.octo.android.rest.client.request.springandroid.RestContentRequest;

public abstract class SpringAndroidContentService extends ContentService {

	public abstract RestTemplate createRestTemplate();

	@Override
	public void addRequest(CachedContentRequest<?> request, Set<RequestListener<?>> listRequestListener) {
		if (request.getContentRequest() instanceof RestContentRequest) {
			((RestContentRequest<?>) request.getContentRequest()).setRestTemplate(createRestTemplate());
		}
		super.addRequest(request, listRequestListener);
	}
}
