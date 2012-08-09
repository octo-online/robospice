package com.octo.android.rest.client.roboguice;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.springframework.web.client.RestTemplate;

import roboguice.RoboGuice;
import android.content.Context;

import com.octo.android.rest.client.persistence.CacheExpiredException;
import com.octo.android.rest.client.persistence.DataPersistenceManager;
import com.octo.android.rest.client.request.CachedContentRequest;
import com.octo.android.rest.client.request.json.RestTemplateFactory;

public abstract class RoboCachedRestRequest<RESULT> extends CachedContentRequest<RESULT> {
	
	private RestTemplateFactory restTemplateFactory;

	public RoboCachedRestRequest(Context context, Class<RESULT> clazz) {
		super(context, clazz, RoboGuice.getInjector(context).getInstance(DataPersistenceManager.class));
		this.restTemplateFactory = RoboGuice.getInjector(context).getInstance(RestTemplateFactory.class);
	}

	protected RestTemplate getRestTemplate() {
		return restTemplateFactory.createRestTemplate();
	}

	@Override
	public RESULT loadDataFromCache(Object cacheFileName)
			throws FileNotFoundException, IOException, CacheExpiredException {
		return getDataPersistenceManager().getDataClassPersistenceManager(getResultType()).loadDataFromCache(cacheFileName, getMaxTimeInCacheBeforeExpiry());
	}

	@Override
	public RESULT saveDataToCacheAndReturnData(RESULT data, Object cacheFileName)
			throws FileNotFoundException, IOException {
		return getDataPersistenceManager().getDataClassPersistenceManager(getResultType()).saveDataToCacheAndReturnData(data, cacheFileName);
	}
	

}
